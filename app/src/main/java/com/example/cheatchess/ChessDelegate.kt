package com.example.cheatchess

interface ChessDelegate {
    fun pieceAt(row: Int, col: Int): ChessPiece?
    fun movePiece(movingPiece: ChessPiece, toRow: Int, toCol: Int)
}