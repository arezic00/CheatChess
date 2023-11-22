package com.example.cheatchess

import android.util.Log

class ChessModel {
    private val pieces = mutableListOf<ChessPiece>()
    var isWhiteTurn = true

    init {
        emptyBoard()
    }

    fun pieceAt(row: Int, col: Int): ChessPiece? {
        for (piece in pieces) {
            if (piece.row == row && piece.col == col)
                return piece
        }
        return null
    }

    fun setStartingPosition() {
        pieces.removeAll(pieces)
        for (i in 0..1) {
            pieces.add(ChessPiece(0, i * 7, R.drawable.black_rook))
            pieces.add(ChessPiece(7, i * 7, R.drawable.white_rook))

            pieces.add(ChessPiece(0, 1 + i * 5, R.drawable.black_knight))
            pieces.add(ChessPiece(7, 1 + i * 5, R.drawable.white_knight))

            pieces.add(ChessPiece(0, 2 + i * 3, R.drawable.black_bishop))
            pieces.add(ChessPiece(7, 2 + i * 3, R.drawable.white_bishop))
        }

        pieces.add(ChessPiece(0, 3, R.drawable.black_queen))
        pieces.add(ChessPiece(7, 3, R.drawable.white_queen))

        pieces.add(ChessPiece(0, 4, R.drawable.black_king))
        pieces.add(ChessPiece(7, 4, R.drawable.white_king))

        for (i in 0..7) {
            pieces.add(ChessPiece(1, i, R.drawable.black_pawn))
            pieces.add(ChessPiece(6, i, R.drawable.white_pawn))
        }
    }

    fun emptyBoard() {
        pieces.removeAll(pieces)
        pieces.add(ChessPiece(0, 4, R.drawable.black_king))
        pieces.add(ChessPiece(7, 4, R.drawable.white_king))

        pieces.add(ChessPiece(-1, 0, R.drawable.black_pawn))
        pieces.add(ChessPiece(-1, 1, R.drawable.black_knight))
        pieces.add(ChessPiece(-1, 2, R.drawable.black_bishop))
        pieces.add(ChessPiece(-1, 3, R.drawable.black_rook))
        pieces.add(ChessPiece(-1, 4, R.drawable.black_queen))

        pieces.add(ChessPiece(8, 0, R.drawable.white_pawn))
        pieces.add(ChessPiece(8, 1, R.drawable.white_knight))
        pieces.add(ChessPiece(8, 2, R.drawable.white_bishop))
        pieces.add(ChessPiece(8, 3, R.drawable.white_rook))
        pieces.add(ChessPiece(8, 4, R.drawable.white_queen))
    }

    fun movePiece(movingPiece: ChessPiece, toRow: Int, toCol: Int) {
        if (movingPiece.row == toRow && movingPiece.col == toCol) return
        val range = 0..7
        if (!(range.contains(toCol)) || !(range.contains(toRow))) {
            if (!isKing(movingPiece) && isOnBoard(movingPiece))
                pieces.remove(movingPiece)
            return
        }
        val toPiece = pieceAt(toRow, toCol)
        if (toPiece != null && isKing(toPiece)) return
        pieces.remove(toPiece)
        if (!isOnBoard(movingPiece)) {
            pieces.add(movingPiece.copy())
        }
        movingPiece.col = toCol
        movingPiece.row = toRow

    }

    private fun isKing(chessPiece: ChessPiece) : Boolean {
        return chessPiece.resID == R.drawable.white_king || chessPiece.resID == R.drawable.black_king
    }

    private fun isOnBoard(chessPiece: ChessPiece) : Boolean {
        return (0..7).contains(chessPiece.row)
    }

    fun changeTurn() {
        isWhiteTurn = !isWhiteTurn
    }

    public fun positionToFEN() : String {
        var positionFEN = ""
        for (row in 0..7) {
            var emptySquareCounter = 0
            for (col in 0..7) {
                val char = pieceToCharacter(pieceAt(row, col))
                if (char == null) {
                    emptySquareCounter++
                    if (col == 7) {
                        positionFEN += emptySquareCounter
                        emptySquareCounter = 0
                    }
                }
                else {
                    if (emptySquareCounter != 0) {
                        positionFEN += emptySquareCounter
                        emptySquareCounter = 0
                    }
                    positionFEN += char
                }
            }
            positionFEN += "/"
        }
        positionFEN = positionFEN.dropLast(1)
        positionFEN += if(isWhiteTurn) " w" else " b"
        positionFEN += " - - 5 11"
        return positionFEN
    }

    private fun pieceToCharacter(chessPiece: ChessPiece?): String? {
        return when (chessPiece?.resID) {
            R.drawable.black_pawn -> "p"
            R.drawable.white_pawn -> "P"
            R.drawable.black_rook -> "r"
            R.drawable.black_knight -> "n"
            R.drawable.black_bishop -> "b"
            R.drawable.black_queen -> "q"
            R.drawable.black_king -> "k"
            R.drawable.white_rook -> "R"
            R.drawable.white_knight -> "K"
            R.drawable.white_bishop -> "B"
            R.drawable.white_queen -> "Q"
            R.drawable.white_king -> "K"
            else -> null
        }
    }
}