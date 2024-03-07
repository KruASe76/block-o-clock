package me.kruase.block_o_clock


object BOCRender {
    fun digitalFont(size: Int): Map<Char, List<List<Boolean>>> {
        val base = SevenSegmentDisplay(List(size) { List(size * 2 - 1) { false } })
        val height = base.height

        return mapOf(
            '0' to base.addSegments(
                Segment.TOP, Segment.LEFT_TOP, Segment.RIGHT_TOP,
                Segment.LEFT_BOTTOM, Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            '1' to base.addSegments(
                Segment.RIGHT_TOP, Segment.RIGHT_BOTTOM
            ).grid,
            '2' to base.addSegments(
                Segment.TOP, Segment.RIGHT_TOP, Segment.MIDDLE, Segment.LEFT_BOTTOM, Segment.BOTTOM
            ).grid,
            '3' to base.addSegments(
                Segment.TOP, Segment.RIGHT_TOP, Segment.MIDDLE, Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            '4' to base.addSegments(
                Segment.LEFT_TOP, Segment.MIDDLE, Segment.RIGHT_TOP, Segment.RIGHT_BOTTOM
            ).grid,
            '5' to base.addSegments(
                Segment.TOP, Segment.LEFT_TOP, Segment.MIDDLE, Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            '6' to base.addSegments(
                Segment.TOP, Segment.LEFT_TOP, Segment.MIDDLE, Segment.LEFT_BOTTOM, Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            '7' to base.addSegments(
                Segment.TOP, Segment.RIGHT_TOP, Segment.RIGHT_BOTTOM
            ).grid,
            '8' to base.addSegments(
                Segment.TOP, Segment.LEFT_TOP, Segment.RIGHT_TOP, Segment.MIDDLE,
                Segment.LEFT_BOTTOM, Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            '9' to base.addSegments(
                Segment.TOP, Segment.LEFT_TOP, Segment.RIGHT_TOP, Segment.MIDDLE,
                Segment.RIGHT_BOTTOM, Segment.BOTTOM
            ).grid,
            ':' to listOf(List(height) { it in listOf(height / 2 - height / 4, height / 2 + height / 4) }),
            '.' to listOf(List(height) { it == height - 1})
        )
    }

    const val MINECRAFT_FONT_WIDTH = 5
    const val MINECRAFT_FONT_HEIGHT = 7

    val minecraftFont: Map<Char, List<List<Boolean>>> = mapOf(
        '0' to """
             ███
            █   █
            █  ██
            █ █ █
            ██  █
            █   █
             ███
        """,
        '1' to """
              █
             ██
              █
              █
              █
              █
            █████
        """,
        '2' to """
             ███
            █   █
                █
              ██
             █
            █   █
            █████
        """,
        '3' to """
             ███
            █   █
                █
              ██
                █
            █   █
             ███
        """,
        '4' to """
               ██
              █ █
             █  █
            █   █
            █████
                █
                █
        """,
        '5' to """
            █████
            █
            ████
                █
                █
            █   █
             ███
        """,
        '6' to """
              ██
             █
            █
            ████
            █   █
            █   █
             ███
        """,
        '7' to """
            █████
            █   █
                █
               █
              █
              █
              █
        """,
        '8' to """
             ███
            █   █
            █   █
             ███
            █   █
            █   █
             ███
        """,
        '9' to """
             ███
            █   █
            █   █
             ████
                █
               █
             ██
        """,
        ':' to """
            
            
            █
            
            
            
            █
        """,
        '.' to """
            
            
            
            
            
            
            █
        """
    )
        .mapValues { (char, string) ->
            string
                .trimIndent()
                .split("\n")
                .map { line ->
                    line
                        .padEnd(if (char.isDigit()) MINECRAFT_FONT_WIDTH else 1)
                        .map { it == '█' }
                }.transpose()  // needed char access: list[x][y]
        }
}


@Suppress("MemberVisibilityCanBePrivate")
@JvmInline
value class SevenSegmentDisplay(val grid: List<List<Boolean>>) {
    val width
        get() = grid.size
    val height
        get() = grid[0].size

    fun addSegments(vararg segments: Segment): SevenSegmentDisplay {
        val newGrid = grid.map { it.toMutableList() }.toMutableList()

        segments.forEach { segment ->
            when (segment) {
                Segment.TOP -> newGrid.map { column -> column[0] = true }
                Segment.MIDDLE -> newGrid.map { column -> column[height / 2] = true }
                Segment.BOTTOM -> newGrid.map { column -> column[height - 1] = true }
                Segment.LEFT_TOP ->
                    newGrid[0] =
                        (List(height / 2 + 1) { true } + newGrid[0].slice(height / 2 + 1 until height))
                            .toMutableList()
                Segment.LEFT_BOTTOM ->
                    newGrid[0] =
                        (newGrid[0].slice(0 until height / 2) + List(height / 2 + 1) { true })
                            .toMutableList()
                Segment.RIGHT_TOP ->
                    newGrid[newGrid.size - 1] =
                        (List(height / 2 + 1) { true } + newGrid[width - 1].slice(height / 2 + 1 until height))
                            .toMutableList()
                Segment.RIGHT_BOTTOM ->
                    newGrid[newGrid.size - 1] =
                        (newGrid[width - 1].slice(0 until height / 2) + List(height / 2 + 1) { true })
                            .toMutableList()
            }
        }

        return SevenSegmentDisplay(newGrid)
    }
}

enum class Segment {
    TOP, MIDDLE, BOTTOM,
    LEFT_TOP, LEFT_BOTTOM,
    RIGHT_TOP, RIGHT_BOTTOM
}
