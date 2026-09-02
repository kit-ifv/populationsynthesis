package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPSolver

data class ORFactoryConfig(
    var solver: MPSolver = MPSolver.createSolver("GLOP"),
    var upperBound: Double = 80_000_000.0,
)