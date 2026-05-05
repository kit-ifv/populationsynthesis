package edu.kit.ifv.populationsynthesis.drafts


//fun interface Refinement {
//    fun refine(partitions: List<Partition>)
//
//    companion object {
//        val NONE = NoRefinement
//
//        val FMRun = FMRun { 1 }
//        val FMThenStomp =
//            domain.synthesis.behavior.householdgeneration.FMThenStomp
//    }
//}
//
//
//class RefinerList(
//    private val refiners: List<Refinement>
//) : Refinement {
//    constructor(vararg refinements: Refinement) : this(refinements.toList())
//    override fun refine(partitions: List<Partition>) {
//        refiners.forEach {
//            it.refine(partitions)
//        }
//    }
//}
//
//
//class SenderPartition(
//    partition: Partition,
//    updater: AttributeUpdater,
//    buckets: BucketList<Move>,
//) : TempPartition(
//    partition,
//    updater,
//    buckets
//) {
//    override val myIncomingMoves: Array<MutableSet<Move>> = emptyArray()
//
//    override val myOutgoingMoves: Array<MutableSet<Move>> = emptyArray()
//
//    override fun initialize(bestTargetTracker: BestTargetTracker) {
//        for (i in partition.signatureTracker.indices) {
//            if (this.partition.amount(SignatureIndex(i)) < 1) continue
//
//            bestTargetTracker.allPartitions.forEach {
//                val move = SymmetricalMoved(this, it, SignatureIndex(i))
//                myOutgoingMoves[i].add(move)
//                it.myIncomingMoves[i].add(move)
//                buckets.insert(move, move.gain)
//            }
//        }
//    }
//
//    val activeSignatures: BooleanArray = BooleanArray(partition.signatureTracker.size) {
//        true
//    }
//    fun emptyElements() = pairs {
//        it.value == 0
//    }
//
//    fun remainingElements() = pairs {
//        it.value > 0
//    }
//
//    private fun pairs(predicate: (IndexedValue<Int>) -> Boolean): List<Pair<SignatureIndex, Int>> = partition.countsList
//        .withIndex()
//        .filter(predicate)
//        .map { SignatureIndex(it.index) to it.value }
//
//    @Suppress("NotImplementedDeclaration")
//    override fun delta(signature: SignatureIndex, amount: Int): List<Move> {
//        TODO()
//    }
//
//    fun operativeDelta(signature: SignatureIndex, amount: Int): SignatureIndex? {
//        require(amount < 0) {
//            "Sender Partition can only send elements. but amount=$amount"
//        }
//        require(-amount <= amount(signature)) {
//            "Wants to move $amount but I only have ${amount(signature)}"
//        }
//
//        partition.delta(signature, amount)
//        if (amount(signature) == 0) {
//            activeSignatures[signature.index] = false
//            return signature
//        }
//        return null
//    }
//
//    override fun toString(): String {
//        return "Sender Partition ${partition.id}"
//    }
//}
//open class TempPartition(
//    val partition: Partition,
//    val updater: AttributeUpdater,
//    val buckets: BucketList<Move>,
//) {
//
//    val signatureTracker get() = partition.signatureTracker
//
//    fun isNotEmpty() = partition.isNotEmpty()
//
//    fun expectedAttributeSum() = partition.expectedSum()
//
//    val id = partition.id
//
//    override fun toString(): String {
//        return "Temp Partition $id"
//    }
//
//    /**
//     * The expected gain when receiving a signature with index i
//     */
//    protected val expectedGains = IntArray(partition.signatureTracker.size) {
//        0
//    }
//
//    fun getGain(index: Int): Int {
//        return expectedGains[index]
//    }
//
//    fun untilFlagChange(sigIdx: Int, searchDirection: Int) = partition.untilFlagChange(sigIdx, searchDirection)
//
//    /**
//     * The expected loss when removing the signature from the partition. Note that this
//     * is not identical to -gain because attributes may differ by given numbers and needs to be calculate4d
//     * individually.
//     */
//    protected val expectedLosses = IntArray(partition.signatureTracker.size) {
//        0
//    }
//
//    fun getLoss(index: Int): Int {
//        return expectedLosses[index]
//    }
//
//    init {
//        for (i in partition.attributeIndices) {
//            val diff = partition.getDelta(
//                i
//            ) // So if diff is positive, I would like to gain elements with that attribute
//
//            // Positive diff, means that the gain table wants to be influenced positively
//            val (updateIdx, change) = updater.getCurrent(i, diff)
//            for (j in change.indices) {
//                expectedGains[updateIdx[j]] += change[j]
//            }
//
//            val (updateIdx2, change2) = updater.getCurrent(i, -diff)
//            for (j in change2.indices) {
//                expectedLosses[updateIdx[j]] += change2[j]
//            }
//        }
//        partition.verify()
//    }
//
//    fun error() = partition.error()
//
//    /**
//     * THe moves. for each signature index i the partition keeps track of where it wants to send the signature index
//     */
//    open val myOutgoingMoves: Array<MutableSet<Move>> = Array<MutableSet<Move>>(partition.signatureTracker.size) {
//        mutableSetOf(Moved(this, this, SignatureIndex(it)))
//    }
//
//    /**
//     * Keeps track of the incoming moves targeting a signature index. Could be that multiple other partitions
//     * want to send signature i to this parititon.
//     */
//    open val myIncomingMoves: Array<MutableSet<Move>> = Array(partition.signatureTracker.size) {
//        mutableSetOf()
//    }
//
//    /**
//     * Performs update immediately. Contracts that even after the delta the gains and losses are accurate.
//     * Return a list of moves that are considered dirty after performing the move.
//     */
//    open fun delta(signature: SignatureIndex, amount: Int): List<Move> {
//        require(partition.getCounts(signature.index) + amount >= 0) {
//            "Thats a too large move, don't please"
//        }
//        hasBeenMoved[signature.index] = true
//
//        val sig = partition.signatureTracker[signature.index]
//
//        val dirtyIndices = sig.entries.filter { partition.getMask(it.key) }.flatMap { (k, factor) ->
//            val currentDelta = partition.getDelta(k)
//            val nextDelta = currentDelta - amount * factor
//            val gainindices = updater.performUpdate(k, currentDelta, nextDelta, expectedGains)
//            val lossindices = updater.performUpdate(k, -currentDelta, -nextDelta, expectedLosses)
//            gainindices + lossindices
//        }.toSet()
//        // Perform actual move after recalculation of gains
//        partition.delta(signature, amount)
//
//        return dirtyIndices.flatMap {
//            myOutgoingMoves[it]
//        } + dirtyIndices.flatMap { myIncomingMoves[it] }
//    }
//
//    fun verifyAll(): Boolean {
//        for (i in partition.signatureTracker.indices) {
//            val t = verifyInternal(i)
//            if (!t) {
//                return false
//            }
//        }
//        return true
//    }
//
//    fun verify(signature: SignatureIndex) = verifyInternal(signature.index)
//    fun calculateAll(): List<Int> {
//        return partition.signatureTracker.indices.map {
//            calculateInternal(it)
//        }
//    }
//
//    fun calculateInternal(sigIdx: Int): Int {
//        val sig = partition.signatureTracker[sigIdx]
//        val trgt = sig.entries.sumOf { (key, value) ->
//            val currentDiff = partition.getDelta(key)
//            min(value, -value + 2 * currentDiff.coerceAtLeast(0))
//        }
//        return trgt
//    }
//
//    fun verifyInternal(sigIdx: Int): Boolean {
//        val trgt = calculateInternal(sigIdx)
//        return trgt == expectedGains[sigIdx]
//    }
//
//    fun updateGains(signature: SignatureIndex) {
//        val sig = partition.signatureTracker[signature.index]
//    }
//
//    // TODO this array is never used.
//    private val hasBeenMoved: BooleanArray = BooleanArray(partition.signatureTracker.size) {
//        false
//    }
//
//    fun reset() {
//        for (i in hasBeenMoved.indices) {
//            hasBeenMoved[i] = false
//        }
//
//        myOutgoingMoves.forEach {
//            it.forEach {
//                it.isLocked = false
//            }
//        }
//    }
//
//    /**
//     * Return the amount of a signature in this partition.
//     * Read it as: How many elements of the signatureIndex are present in the partition.
//     */
//    fun amount(signature: SignatureIndex): Int {
//        return partition.amount(signature)
//    }
//
//    fun add(signature: SignatureIndex, amount: Int) {
//        delta(signature, amount)
//    }
//
//    fun remove(signature: SignatureIndex, amount: Int) {
//        delta(signature, -amount)
//    }
//
//    /**
//     * Initializes the moves based on the best target partition from the target tracker.
//     */
//    open fun initialize(bestTargetTracker: BestTargetTracker) {
//        for (i in partition.signatureTracker.indices) {
//            if (this.partition.amount(SignatureIndex(i)) < 1) continue
//            val targetPartition = bestTargetTracker.getRandom(i)
//
//            myOutgoingMoves[i].apply {
//                val move = first()
//                move.apply {
//                    to = targetPartition
//                    targetPartition.myIncomingMoves[i].add(this)
//                    buckets.insert(this, gain)
//                }
//            }
//        }
//    }
//}
//class AlternateBetweenFMAndStomp(
//    private val passes: Int = 10
//) : Refinement {
//
//    val fmStep = FMRun(
//        amountOfPasses = 10
//    ) {
//
//        min(
//            it.from.amount(it.signatureIndex),
//            min(
//                it.from.untilFlagChange(it.signatureIndex.index, -1),
//                it.to.untilFlagChange(it.signatureIndex.index, 1)
//            )
//        )
//    }
//
//    val stompStep =
//        AttributeStomper(repetitions = 10)
//    override fun refine(partitions: List<Partition>) {
//        println("InitialFM")
//        fmStep.refine(partitions)
//        println(partitions.averagePercentError())
//        repeat(passes) {
//            println("Stomping")
//            stompStep.refine(partitions)
//            println(partitions.averagePercentError())
//            println("FMIng")
//            fmStep.refine(partitions)
//            println(partitions.averagePercentError())
//        }
//    }
//}
//@Suppress("MagicNumber")
//fun Partition.eval(): Double {
//    val elements = countsList.sum()
//    val bias = elements.toDouble() / (elements + 1000)
//    return relativeErrors().max() * bias
//}
//
///**
// * Flatten the worst attribute misrepresentation in the partition space by forcibly adding or removing
// * random elements from other partitions until the attribute is corrected.
// */
//
//class AttributeStomper(
//    private val repetitions: Int = 4,
//    private val random: Random = Random(1),
//    val stompTargetSelector: (
//        Collection<Partition>
//    ) -> Collection<Partition> = {
//        it.sortedByDescending { p ->
//
//            p.eval()
//        }
//    },
//) : Refinement {
//
//    override fun refine(partitions: List<Partition>) {
//        repeat(repetitions) {
//            val worstPartition = stompTargetSelector(partitions).first()
//
//            val targetAttributeIndex = worstPartition.worstRelativeIndex()
//            val errorSign = worstPartition.errorFor(targetAttributeIndex).sign
//
//            val sortedPartitions = partitions.sortedBy { it.errorFor(targetAttributeIndex) }.filter {
//                it !== worstPartition && it.errorFor(targetAttributeIndex).sign != errorSign
//            }
//
//            if (errorSign == 1) {
//                resolveOverload(worstPartition, targetAttributeIndex, sortedPartitions)
//            } else {
//                resolveUnderload(worstPartition, targetAttributeIndex, sortedPartitions)
//            }
//        }
//    }
//
//    // In this instance we want to remove elements from the partition, preferably equally distributed because
//    // We have no idea about other qualities. This means that we want to transfer roughly equally to each
//    // receiving partition
//    private fun resolveOverload(
//        worstPartition: Partition,
//        attrIdx: Int,
//        suitableCandidates: List<Partition>
//    ) {
//        suitableCandidates.shuffled(random).forEach { candidate ->
//            intern(worstPartition, candidate, attrIdx)
//        }
//    }
//
//    // In this case we want to add elements to the partition. The other sender partitions have a smaller error by
//    // default, so in this case we need to make sure that the transfer error is maintained, and sending a roughly
//    // equal amount of the elements from the target partition.
//    private fun resolveUnderload(
//        worstPartition: Partition,
//        attrIdx: Int,
//        suitableCandidates: List<Partition>
//    ) {
//        suitableCandidates.shuffled(random).forEach {
//            intern(it, worstPartition, attrIdx)
//        }
//    }
//
//    private fun intern(
//        senderPartition: Partition,
//        receiverPartition: Partition,
//        attrIdx: Int
//    ) {
//        val transferTargets = senderPartition.currentElementsForAttribute(attrIdx)
//        val maxTransferAmount: Int = min(
//            senderPartition.errorFor(attrIdx),
//            -receiverPartition.errorFor(attrIdx)
//        )
//
//        val sum = transferTargets.sumOf { it.amountOfElements }
//
//        val desiredTransferShares = transferTargets.associateWith { target ->
//            (target.amountOfElements).toDouble() / sum
//        }
//
//        val integerTransferAmount = desiredTransferShares.entries.sumOf { (k, v) ->
//            // This target is rounded down
//            val integerTarget = (v * maxTransferAmount).toInt() / k.impact
//            if (integerTarget != 0) {
//                senderPartition.transferTo(
//                    receiverPartition,
//                    k.signatureIndex,
//                    integerTarget
//                )
//            }
//
//            integerTarget * k.impact
//        }
//
//        require(integerTransferAmount <= maxTransferAmount) {
//            "This is unacceptable"
//        }
//        var remainingTransferTarget = maxTransferAmount - integerTransferAmount
//        val remainingTargets = senderPartition.currentElementsForAttribute(attrIdx).shuffled(random).toMutableList()
//        var i = 0
//        while (remainingTransferTarget > 0 && remainingTargets.isNotEmpty()) {
//            val target = remainingTargets[i % remainingTargets.size]
//            if (target.impact > remainingTransferTarget || target.isEmpty()) {
//                remainingTargets.remove(target)
//                continue
//            }
//
//            val desiredTransfers = 1
//            remainingTransferTarget -= desiredTransfers * target.impact
//            target.amountOfElements -= desiredTransfers
//            senderPartition.transferTo(
//                receiverPartition,
//                target.signatureIndex,
//                desiredTransfers
//            )
//            i++
//        }
//    }
//}
//
///**
// * Keeps track which partition is currently the best to receive a signature based on the calculated gain.
// */
//class BestTargetTracker(val allPartitions: Collection<TempPartition>, private val random: Random = Random(1)) {
//
//    private val maxGain: Int = allPartitions.first().signatureTracker.largestDifference
//
//    private val signatureBuckets: Array<BucketList<TempPartition>> = allPartitions.first()
//        .signatureTracker.indices.map {
//            BucketList<TempPartition>(maxGain)
//        }.toTypedArray()
//
//    init {
//        signatureBuckets.withIndex().forEach { (i, bucket) ->
//            allPartitions.forEach { partition ->
//                bucket.insert(partition, partition.getGain(i))
//            }
//        }
//    }
//
//    fun isBest(sigIdx: Int, partition: TempPartition): Boolean {
//        return signatureBuckets[sigIdx].isBest(partition)
//    }
//    fun getRandom(sigIdx: Int): TempPartition {
//        return signatureBuckets[sigIdx].randomBest(random)
//    }
//
//    fun getFirst(sigIdx: Int): TempPartition {
//        return signatureBuckets[sigIdx].pollBest()
//    }
//
//    // TODO theoretically the updater does not need to change all sigBuckets, but just the ones that actually changed
//    fun update(tempPartition: TempPartition) {
//        signatureBuckets.withIndex().forEach { (i, bucket) ->
//            bucket.update(tempPartition, tempPartition.getGain(i))
//        }
//    }
//}
//
//typealias Bucket<T> = MutableSet<T>
//val FMThenStomp = RefinerList(
//    FMRun(
//        amountOfPasses = 100
//    ) {
//        1
//    },
//
//    AttributeStomper(),
//    FMRun(
//        amountOfPasses = 100
//    ) {
//        1
//    }
//
//)
//class MoveRecalculator(
//    val bestTargetTracker: BestTargetTracker
//) {
//    /**
//     * Updates dirty moves by removing them from the bucketlist and readding them based on the gain. Checks in the
//     * best target tracker what the best target partition is.
//     */
//    fun recalculate(buckets: BucketList<Move>, dirtyMoves: Collection<Move>) {
//        if (dirtyMoves.isEmpty()) return
//
//        dirtyMoves.forEach {
//            buckets.remove(it)
//        }
//
//        dirtyMoves.filter { !it.isEmpty && !it.isLocked }.forEach {
//            val sigIdx = it.signatureIndex.index
//            if (!bestTargetTracker.isBest(sigIdx, it.to)) {
//                val bestTarget: TempPartition = bestTargetTracker.getRandom(
//                    sigIdx
//                )
//                it.to.myIncomingMoves[sigIdx].remove(it)
//                it.to = bestTarget
//                it.to.myIncomingMoves[sigIdx].add(it)
//            }
//
//            buckets.insert(it, it.gain)
//        }
//    }
//}
//
//class BucketList<T>(
//    private val maxGain: Int
//) {
//    private val positionTracker: MutableMap<T, Int> = mutableMapOf()
//    private var bestBucketIndex: Int = -1
//    private val buckets: Array<Bucket<T>> = Array(2 * maxGain + 1) {
//        mutableSetOf()
//    }
//
//    private fun offsetGain(gain: Int): Int {
//        return gain + maxGain
//    }
//
//    fun insert(element: T, gain: Int) {
//        val idx = offsetGain(gain)
//        if (idx !in buckets.indices) {
//            throw IndexOutOfBoundsException("No Bucket with that index")
//        }
//        buckets[idx].add(element)
//        positionTracker[element] = idx
//        if (idx > bestBucketIndex) bestBucketIndex = idx
//    }
//
//    fun remove(element: T) {
//        positionTracker[element]?.let {
//            val targetBucket = buckets[it]
//            targetBucket.remove(element)
//            positionTracker.remove(element)
//
//            if (it == bestBucketIndex && targetBucket.isEmpty()) {
//                updateBestBucketIndex()
//            }
//        }
//    }
//
//    @Deprecated("This method is slow and should only be used for debugging")
//    operator fun contains(element: T): Boolean {
//        return buckets.any { element in it }
//    }
//    private fun updateBestBucketIndex() {
//        var idx = bestBucketIndex
//        while (idx >= 0 && buckets[idx].isEmpty()) {
//            idx--
//        }
//        bestBucketIndex = idx // Will set idx -1 if the entire datastructure is empty.
//    }
//
//    fun update(element: T, newGain: Int) {
//        remove(element)
//        insert(element, newGain)
//    }
//    fun pollBest(): T {
//        return buckets[bestBucketIndex].first()
//    }
//
//    fun randomBest(random: Random): T {
//        return buckets[bestBucketIndex].random(random)
//    }
//
//    fun isEmpty(): Boolean = bestBucketIndex <= -1
//
//    fun popBest(): Pair<T, Int>? {
//        if (isEmpty()) return null
//        val element = buckets[bestBucketIndex].first()
//        val gain = currentGain()
//        remove(element)
//        return element to gain
//    }
//
//    fun currentGain() = bestBucketIndex - maxGain
//
//    fun validateBuckets(predicate: (Set<T>, Int) -> Boolean): Boolean {
//        return buckets.withIndex().all { predicate(it.value, it.index - maxGain) }
//    }
//
//    fun <X> operateOnBuckets(predicate: (Set<T>, Int) -> Collection<X>): Set<X> {
//        return buckets.withIndex().flatMap { predicate(it.value, it.index - maxGain) }.toSet()
//    }
//
//    fun elements() = buckets.flatMap { it }
//
//    fun validateElements(predicate: (T) -> Boolean): Boolean {
//        return elements().all(predicate)
//    }
//    fun isBest(element: T): Boolean {
//        return element in buckets[bestBucketIndex]
//    }
//    fun clear() {
//        buckets.forEach { it.clear() }
//    }
//}
//
