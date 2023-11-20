package com.example.cheatchess

class ChessModel {
    private val pieces = mutableListOf<ChessPiece>()

    init {
        setStartingPosition()
    }

    fun pieceAt(row: Int, col: Int): ChessPiece? {
        for (piece in pieces) {
            if (piece.row == row && piece.col == col)
                return piece
        }
        return null
    }

    private fun setStartingPosition() {
        emptyBoard()
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

    private fun emptyBoard() {
        pieces.removeAll(pieces)
    }

    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        if (fromRow == toRow && fromCol == toCol) return
        val movingPiece = pieceAt(fromRow, fromCol) ?: return
        pieceAt(toRow, toCol).let { pieces.remove(it) }
        movingPiece.col = toCol
        movingPiece.row = toRow
    }
}