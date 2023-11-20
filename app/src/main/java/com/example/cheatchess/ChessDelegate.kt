package com.example.cheatchess

interface ChessDelegate {
    fun pieceAt(row: Int, col: Int) : ChessPiece?
    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int)
}