package com.example.cheatchess

import com.example.cheatchess.Constants.BOARD_RANGE
import com.example.cheatchess.Constants.MAX_INDEX
import com.example.cheatchess.Constants.MIN_INDEX
import com.example.cheatchess.Constants.OUTSIDE_INDEX_ABOVE
import com.example.cheatchess.Constants.OUTSIDE_INDEX_BELOW

class ChessModel {
    private val pieces = mutableListOf<ChessPiece>()
    var isWhiteTurn = true
    var canCastleKingSideWhite = false
    var canCastleQueenSideWhite = false
    var canCastleKingSideBlack = false
    var canCastleQueenSideBlack = false

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
            pieces.add(ChessPiece(MIN_INDEX, i * 7, R.drawable.black_rook))
            pieces.add(ChessPiece(MAX_INDEX, i * 7, R.drawable.white_rook))

            pieces.add(ChessPiece(MIN_INDEX, 1 + i * 5, R.drawable.black_knight))
            pieces.add(ChessPiece(MAX_INDEX, 1 + i * 5, R.drawable.white_knight))

            pieces.add(ChessPiece(MIN_INDEX, 2 + i * 3, R.drawable.black_bishop))
            pieces.add(ChessPiece(MAX_INDEX, 2 + i * 3, R.drawable.white_bishop))
        }

        pieces.add(ChessPiece(MIN_INDEX, 3, R.drawable.black_queen))
        pieces.add(ChessPiece(MAX_INDEX, 3, R.drawable.white_queen))

        pieces.add(ChessPiece(MIN_INDEX, 4, R.drawable.black_king))
        pieces.add(ChessPiece(MAX_INDEX, 4, R.drawable.white_king))

        for (i in BOARD_RANGE) {
            pieces.add(ChessPiece(MIN_INDEX + 1, i, R.drawable.black_pawn))
            pieces.add(ChessPiece(MAX_INDEX - 1, i, R.drawable.white_pawn))
        }
    }

    fun emptyBoard() {
        pieces.removeAll(pieces)
        pieces.add(ChessPiece(MIN_INDEX, 4, R.drawable.black_king))
        pieces.add(ChessPiece(MAX_INDEX, 4, R.drawable.white_king))

        pieces.add(ChessPiece(OUTSIDE_INDEX_ABOVE, 0, R.drawable.black_pawn))
        pieces.add(ChessPiece(OUTSIDE_INDEX_ABOVE, 1, R.drawable.black_knight))
        pieces.add(ChessPiece(OUTSIDE_INDEX_ABOVE, 2, R.drawable.black_bishop))
        pieces.add(ChessPiece(OUTSIDE_INDEX_ABOVE, 3, R.drawable.black_rook))
        pieces.add(ChessPiece(OUTSIDE_INDEX_ABOVE, 4, R.drawable.black_queen))

        pieces.add(ChessPiece(OUTSIDE_INDEX_BELOW, 0, R.drawable.white_pawn))
        pieces.add(ChessPiece(OUTSIDE_INDEX_BELOW, 1, R.drawable.white_knight))
        pieces.add(ChessPiece(OUTSIDE_INDEX_BELOW, 2, R.drawable.white_bishop))
        pieces.add(ChessPiece(OUTSIDE_INDEX_BELOW, 3, R.drawable.white_rook))
        pieces.add(ChessPiece(OUTSIDE_INDEX_BELOW, 4, R.drawable.white_queen))

    }

    fun movePiece(movingPiece: ChessPiece, toRow: Int, toCol: Int) {
        if (movingPiece.row == toRow && movingPiece.col == toCol) return
        if (!(BOARD_RANGE.contains(toCol)) || !(BOARD_RANGE.contains(toRow))) {
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
        return (BOARD_RANGE).contains(chessPiece.row)
    }

    fun changeTurn() {
        isWhiteTurn = !isWhiteTurn
    }

    fun positionToFEN() : String {
        var positionFEN = ""
        for (row in BOARD_RANGE) {
            var emptySquareCounter = 0
            for (col in BOARD_RANGE) {
                val char = pieceToCharacter(pieceAt(row, col))
                if (char == null) {
                    emptySquareCounter++
                    if (col == MAX_INDEX) {
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
        setCastlingRights()
        positionFEN += " ${castlingRightsToString()} - 0 1"
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
            R.drawable.white_knight -> "N"
            R.drawable.white_bishop -> "B"
            R.drawable.white_queen -> "Q"
            R.drawable.white_king -> "K"
            else -> null
        }
    }

    private fun setCastlingRightsWhite() {
        var piece = pieceAt(7,4)
        if (piece != null && piece.resID == R.drawable.white_king) {
            piece = pieceAt(7,0)
            canCastleQueenSideWhite = piece != null && piece.resID == R.drawable.white_rook
            piece = pieceAt(7,7)
            canCastleKingSideWhite = piece != null && piece.resID == R.drawable.white_rook
        }
        else {
            canCastleKingSideWhite = false
            canCastleQueenSideWhite = false
        }
    }

    private fun setCastlingRightsBlack() {
        var piece = pieceAt(0,4)
        if (piece != null && piece.resID == R.drawable.black_king) {
            piece = pieceAt(0,0)
            canCastleQueenSideBlack = piece != null && piece.resID == R.drawable.black_rook
            piece = pieceAt(0,7)
            canCastleKingSideBlack = piece != null && piece.resID == R.drawable.black_rook
        }
        else {
            canCastleKingSideBlack = false
            canCastleQueenSideBlack = false
        }
    }

    private fun setCastlingRights() {
        setCastlingRightsWhite()
        setCastlingRightsBlack()
    }

    private fun castlingRightsToString(): String {
        var result = ""
        if (canCastleKingSideWhite) result += "K"
        if (canCastleQueenSideWhite) result += "Q"
        if (canCastleKingSideBlack) result += "k"
        if (canCastleQueenSideBlack) result += "q"
        return if (result == "") "-" else result
    }
}