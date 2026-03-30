import sys
import numpy as np
from scipy.optimize import nnls, lsq_linear


def main():
    if len(sys.argv) != 7:
        raise SystemExit(
            "Usage: python nnls_worker.py <rows> <cols> <A.bin> <b.bin> <x.bin> <v.bin>"
        )

    rows = int(sys.argv[1])
    cols = int(sys.argv[2])
    a_path = sys.argv[3]
    b_path = sys.argv[4]
    x_path = sys.argv[5]
    start_path = sys.argv[6]


    A = np.fromfile(a_path, dtype="<f8")
    b = np.fromfile(b_path, dtype="<f8")
    start = np.fromfile(start_path, dtype="<f8")

    expected_a = rows * cols
    expected_b = rows

    if A.size != expected_a:
        raise ValueError(f"A size mismatch: got {A.size}, expected {expected_a}")
    if b.size != expected_b:
        raise ValueError(f"b size mismatch: got {b.size}, expected {expected_b}")

    A = A.reshape((cols, rows)).T
    b = b.reshape((rows,))
    start = start.reshape((cols,))

    lower_bounds = np.zeros(cols)
    upper_bounds = start * 2.0 * 4314

    x = lsq_linear(A, b,  bounds=(lower_bounds, upper_bounds)).x

    # x, rnorm = nnls(A, b)


    x.astype("<f8").tofile(x_path)

def nnls_l2(A, b, lam = 1e-2):
    m, n = A.shape
    A_aug = np.vstack([A, np.sqrt(lam) * np.eye(n)])
    b_aug = np.concatenate([b, np.zeros(n)])
    res = lsq_linear(A_aug, b_aug, bounds=(0, np.inf))
    return res.x
if __name__ == "__main__":
    main()