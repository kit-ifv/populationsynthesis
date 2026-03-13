package edu.kit.ifv.populationsynthesis.algorithms

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.NormOps_DDRM
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

data class LsqrResult(
    val x: DoubleArray,
    val istop: Int,
    val itn: Int,
    val r1norm: Double,
    val r2norm: Double,
    val anorm: Double,
    val acond: Double,
    val arnorm: Double,
    val xnorm: Double,
    val varDiag: DoubleArray
)

private const val EPS = 2.220446049250313e-16// similar role as numpy.finfo(float).eps-ish

private data class SymOrtho(val cs: Double, val sn: Double, val r: Double)

/**
 * Stable symmetric Givens rotation.
 * Equivalent to SciPy's internal _sym_ortho(a, b).
 */
private fun symOrtho(a: Double, b: Double): SymOrtho {
    if (b == 0.0) return SymOrtho(cs = if (a == 0.0) 1.0 else kotlin.math.sign(a), sn = 0.0, r = abs(a))
    if (a == 0.0) return SymOrtho(cs = 0.0, sn = kotlin.math.sign(b), r = abs(b))

    val r = hypot(a, b)
    val cs = a / r
    val sn = b / r
    return SymOrtho(cs, sn, r)
}

/**
 * Find the least-squares solution to a large, sparse, linear system
 * of equations.
 *
 * The function solves ``Ax = b``  or  ``min ||Ax - b||^2`` or
 * ``min ||Ax - b||^2 + d^2 ||x - x0||^2``.
 *
 * The matrix A may be square or rectangular (over-determined or
 * under-determined), and may have any rank.
 *
 * ::
 *
 *   1. Unsymmetric equations --    solve  Ax = b
 *
 *   2. Linear least squares  --    solve  Ax = b
 *                                  in the least-squares sense
 *
 *   3. Damped least squares  --    solve  (   A    )*x = (    b    )
 *                                         ( damp*I )     ( damp*x0 )
 *                                  in the least-squares sense
 */
fun lsqr(
    A: DMatrixRMaj,                 // m-by-n dense matrix (you can swap for a LinearOperator later)
    b: DoubleArray,                 // shape (m,)
    damp: Double = 0.0,
    atol: Double = 1e-6,
    btol: Double = 1e-6,
    conlim: Double = 1e8,
    iterLim: Int? = null,
    show: Boolean = false,
    calcVar: Boolean = false,
    x0: DoubleArray? = null
): LsqrResult {

    // b = np.atleast_1d(b) ... squeeze not needed for DoubleArray
    val m = A.numRows
    val n = A.numCols
    require(b.size == m) { "b has size ${b.size} but A has $m rows" }

    val iter_lim = iterLim ?: (2 * n)
    val varDiag = DoubleArray(n) { 0.0 }

    val msg = arrayOf(
        "The exact solution is  x = 0                              ",
        "Ax - b is small enough, given atol, btol                  ",
        "The least-squares solution is good enough, given atol     ",
        "The estimate of cond(Abar) has exceeded conlim            ",
        "Ax - b is small enough for this machine                   ",
        "The least-squares solution is good enough for this machine",
        "Cond(Abar) seems to be too large for this machine         ",
        "The iteration limit has been reached                      "
    )

    if (show) {
        println()
        println("LSQR            Least-squares solution of  Ax = b")
        val str1 = "The matrix A has $m rows and $n columns"
        val str2 = "damp = ${"%.14e".format(damp)}   calc_var = ${calcVar}"
        val str3 = "atol = ${"%.2e".format(atol)}                 conlim = ${"%.2e".format(conlim)}"
        val str4 = "btol = ${"%.2e".format(btol)}               iter_lim = $iter_lim"
        println(str1)
        println(str2)
        println(str3)
        println(str4)
    }

    var itn = 0
    var istop = 0
    var ctol = 0.0
    if (conlim > 0) ctol = 1.0 / conlim
    var anorm = 0.0
    var acond = 0.0
    val dampsq = damp * damp
    var ddnorm = 0.0
    var res2 = 0.0
    var xnorm = 0.0
    var xxnorm = 0.0
    var z = 0.0
    var cs2 = -1.0
    var sn2 = 0.0

    val uArr = DoubleArray(m)
    val u = DMatrixRMaj(m, 1, true, *b)

    val bnorm = sqrt(b.sumOf { it * it })
    var beta: Double = bnorm
    // EJML column vectors backed by arrays (we explicitly manage data to avoid per-iteration allocations)
    val xArrr = if (x0 == null) {

        DoubleArray(n)
    } else x0.copyOf()
    val x = DMatrixRMaj(n, 1, true, *xArrr)
    var alfa: Double


    val v = DMatrixRMaj(n, 1, true, *DoubleArray(n))
    val w = DMatrixRMaj(n, 1, true, *DoubleArray(n))
    if (beta > 0) {
        // u = (1/beta) * u
        CommonOps_DDRM.scale(1.0 / beta, u)

        // v = A.rmatvec(u)
        CommonOps_DDRM.multTransA(A, u, v)              // v = A^T*u
        alfa = NormOps_DDRM.normF(v)
    } else {
        // v = x.copy(); alfa = 0
        v.setTo(x)
//        CommonOps_DDRM.copy(x, v)
        alfa = 0.0
    }
//    if (x0 == null) {
//        // x = zeros(n)
//        // beta = bnorm.copy()
//        beta = bnorm
//    } else {
//        // u = u - A.matvec(x)
//        CommonOps_DDRM.mult(A, x, tmpM)                 // tmpM = A*x
//        CommonOps_DDRM.subtractEquals(u, tmpM)          // u = u - tmpM
//        beta = NormOps_DDRM.normF(u)                    // ||u||
//    }


    val tmpM = DMatrixRMaj(m, 1)
    val tmpN = DMatrixRMaj(n, 1)

    // Set up the first vectors u and v for the bidiagonalization.
    // These satisfy  beta*u = b - A@x,  alfa*v = A'@u.
    // u = b
    for (i in 0 until m) uArr[i] = b[i]






    if (alfa > 0) {
        // v = (1/alfa) * v
        CommonOps_DDRM.scale(1.0 / alfa, v)
    }
    // w = v.copy()
    w.setTo(v)
//    CommonOps_DDRM.copy(v, w)

    var rhobar = alfa
    var phibar = beta
    var rnorm = beta
    var r1norm = rnorm
    var r2norm = rnorm

    // Reverse the order here from the original matlab code because
    // there was an error on return when arnorm==0
    var arnorm = alfa * beta
    if (arnorm == 0.0) {
        if (show) println(msg[0])
        return LsqrResult(
            x = x.data,
            istop = istop,
            itn = itn,
            r1norm = r1norm,
            r2norm = r2norm,
            anorm = anorm,
            acond = acond,
            arnorm = arnorm,
            xnorm = xnorm,
            varDiag = varDiag
        )
    }

    if (show) {
        val head1 = "   Itn      x[0]       r1norm     r2norm "
        val head2 = " Compatible    LS      Norm A   Cond A"
        println()
        println(head1 + head2)
        val test1 = 1.0
        val test2 = alfa / beta
        val s1 = "%6d %12.5e".format(itn, x.data[0])
        val s2 = " %10.3e %10.3e".format(r1norm, r2norm)
        val s3 = "  %8.1e %8.1e".format(test1, test2)
        println(s1 + s2 + s3)
    }

    // Main iteration loop.
    while (itn < iter_lim) {
        itn += 1

        // Perform the next step of the bidiagonalization to obtain the
        // next  beta, u, alfa, v. These satisfy the relations
        //     beta*u  =  A@v   -  alfa*u,
        //     alfa*v  =  A'@u  -  beta*v.
        CommonOps_DDRM.mult(A, v, tmpM)                 // tmpM = A*v
        CommonOps_DDRM.addEquals(tmpM, -alfa, u)         // tmpM = tmpM - alfa*u

        u.setTo(tmpM)
//        CommonOps_DDRM.copy(tmpM, u)                     // u = tmpM

        beta = NormOps_DDRM.normF(u)

        if (beta > 0) {
            CommonOps_DDRM.scale(1.0 / beta, u)
            anorm = sqrt(anorm * anorm + alfa * alfa + beta * beta + dampsq)

            CommonOps_DDRM.multTransA(A, u, tmpN)        // tmpN = A^T*u
            CommonOps_DDRM.addEquals(tmpN, -beta, v)      // tmpN = tmpN - beta*v
            v.setTo(tmpN)
//            CommonOps_DDRM.copy(tmpN, v)                  // v = tmpN

            alfa = NormOps_DDRM.normF(v)
            if (alfa > 0) {
                CommonOps_DDRM.scale(1.0 / alfa, v)
            }
        }

        // Use a plane rotation to eliminate the damping parameter.
        // This alters the diagonal (rhobar) of the lower-bidiagonal matrix.
        val rhobar1: Double
        val psi: Double
        if (damp > 0) {
            rhobar1 = sqrt(rhobar * rhobar + dampsq)
            val cs1 = rhobar / rhobar1
            val sn1 = damp / rhobar1
            psi = sn1 * phibar
            phibar = cs1 * phibar
        } else {
            // cs1 = 1 and sn1 = 0
            rhobar1 = rhobar
            psi = 0.0
        }

        // Use a plane rotation to eliminate the subdiagonal element (beta)
        // of the lower-bidiagonal matrix, giving an upper-bidiagonal matrix.
        val (cs, sn, rho) = symOrtho(rhobar1, beta)

        val theta = sn * alfa
        rhobar = -cs * alfa
        val phi = cs * phibar
        phibar = sn * phibar
        val tau = sn * phi

        // Update x and w.
        val t1 = phi / rho
        val t2 = -theta / rho

        // dk = (1 / rho) * w
        // x = x + t1 * w
        // w = v + t2 * w
        // ddnorm = ddnorm + norm(dk)^2
        // if calc_var: var += dk^2
        val invRho = 1.0 / rho

        // x += t1*w
        CommonOps_DDRM.addEquals(x, t1, w)

        // compute dk norm and optionally dk^2 for var
        // dk = invRho * w_old. Note: w has not been updated yet.
        // We'll compute using wArr directly.
        var dkNorm2 = 0.0
        if (calcVar) {
            for (i in 0 until n) {
                val dk = invRho * w[i]
                dkNorm2 += dk * dk
                varDiag[i] += dk * dk
            }
        } else {
            for (i in 0 until n) {
                val dk = invRho * w[i]
                dkNorm2 += dk * dk
            }
        }
        ddnorm += dkNorm2

        // w = v + t2*w
        CommonOps_DDRM.scale(t2, w)      // w = t2*w
        CommonOps_DDRM.addEquals(w, 1.0, v) // w = w + v

        // Use a plane rotation on the right to eliminate the
        // super-diagonal element (theta) of the upper-bidiagonal matrix.
        // Then use the result to estimate norm(x).
        val delta = sn2 * rho
        val gambar = -cs2 * rho
        val rhs = phi - delta * z
        val zbar = rhs / gambar
        xnorm = sqrt(xxnorm + zbar * zbar)
        val gamma = sqrt(gambar * gambar + theta * theta)
        cs2 = gambar / gamma
        sn2 = theta / gamma
        z = rhs / gamma
        xxnorm += z * z

        // Test for convergence.
        // First, estimate the condition of the matrix  Abar,
        // and the norms of  rbar  and  Abar'rbar.
        acond = anorm * sqrt(ddnorm)
        val res1 = phibar * phibar
        res2 += psi * psi
        rnorm = sqrt(res1 + res2)
        arnorm = alfa * abs(tau)

        // Distinguish between
        //    r1norm = ||b - Ax|| and
        //    r2norm = rnorm in current code
        //           = sqrt(r1norm^2 + damp^2*||x - x0||^2).
        //    Estimate r1norm from
        //    r1norm = sqrt(r2norm^2 - damp^2*||x - x0||^2).
        // Although there is cancellation, it might be accurate enough.
        if (damp > 0) {
            val r1sq = rnorm * rnorm - dampsq * xxnorm
            r1norm = sqrt(abs(r1sq))
            if (r1sq < 0) r1norm = -r1norm
        } else {
            r1norm = rnorm
        }
        r2norm = rnorm

        // Now use these norms to estimate certain other quantities,
        // some of which will be small near a solution.
        val test1 = rnorm / bnorm
        val test2 = arnorm / (anorm * rnorm + EPS)
        val test3 = 1.0 / (acond + EPS)
        val t1test = test1 / (1.0 + anorm * xnorm / bnorm)
        val rtol = btol + atol * anorm * xnorm / bnorm

        // The following tests guard against extremely small values of
        // atol, btol  or  ctol.  (The user may have set any or all of
        // the parameters  atol, btol, conlim  to 0.)
        // The effect is equivalent to the normal tests using
        // atol = eps,  btol = eps,  conlim = 1/eps.
        if (itn >= iter_lim) istop = 7
        if (1.0 + test3 <= 1.0) istop = 6
        if (1.0 + test2 <= 1.0) istop = 5
        if (1.0 + t1test <= 1.0) istop = 4

        // Allow for tolerances set by the user.
        if (test3 <= ctol) istop = 3
        if (test2 <= atol) istop = 2
        if (test1 <= rtol) istop = 1

        if (show) {
            // See if it is time to print something.
            var prnt = false
            if (n <= 40) prnt = true
            if (itn <= 10) prnt = true
            if (itn >= iter_lim - 10) prnt = true
            // if (itn%10 == 0) prnt = true
            if (test3 <= 2 * ctol) prnt = true
            if (test2 <= 10 * atol) prnt = true
            if (test1 <= 10 * rtol) prnt = true
            if (istop != 0) prnt = true

            if (prnt) {
                val s1 = "%6d %12.5e".format(itn, x.data[0])
                val s2 = " %10.3e %10.3e".format(r1norm, r2norm)
                val s3 = "  %8.1e %8.1e".format(test1, test2)
                val s4 = " %8.1e %8.1e".format(anorm, acond)
                println(s1 + s2 + s3 + s4)
            }
        }

        if (istop != 0) break
    }

    // End of iteration loop.
    // Print the stopping condition.
    if (show) {
        println()
        println("LSQR finished")
        println(msg[istop])
        println()
        val s1 = "istop =${"%8d".format(istop)}   r1norm =${"%8.1e".format(r1norm)}"
        val s2 = "anorm =${"%8.1e".format(anorm)}   arnorm =${"%8.1e".format(arnorm)}"
        val s3 = "itn   =${"%8d".format(itn)}   r2norm =${"%8.1e".format(r2norm)}"
        val s4 = "acond =${"%8.1e".format(acond)}   xnorm  =${"%8.1e".format(xnorm)}"
        println("$s1   $s2")
        println("$s3   $s4")
        println()
    }

    return LsqrResult(
        x = x.data,
        istop = istop,
        itn = itn,
        r1norm = r1norm,
        r2norm = r2norm,
        anorm = anorm,
        acond = acond,
        arnorm = arnorm,
        xnorm = xnorm,
        varDiag = varDiag
    )
}

