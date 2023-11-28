package com.example.cheatchess

import android.util.Log
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Field

class ChessModelTest {
    private fun invokePrivateMethod(classInstance: Any, method: String) {
        classInstance::class.java.getDeclaredMethod(method).apply {
            this.isAccessible = true
            invoke(classInstance)
        }
    }

    private fun setPrivateField(classInstance: Any, field: String, value: Any) {
        classInstance::class.java.getDeclaredField(field).apply {
            isAccessible = true
            set(classInstance, value)
        }
    }

    private fun <T> getPrivateField(classInstance: Any, field: String, returnType: Class<T>) : T {
        classInstance::class.java.getDeclaredField(field).apply {
            isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return get(classInstance) as T
        }
    }

    @Test
    fun `white king not on starting position - castlings white side are false`() {
        val chessModel = ChessModel()
        setPrivateField(chessModel, "pieces", mutableListOf(
            ChessPiece(7,3,R.drawable.white_king)))
        invokePrivateMethod(chessModel,"setCastlingRightsWhite")

        assertFalse(getPrivateField(chessModel,"canCastleKingSideWhite", Boolean::class.java))
        assertFalse(getPrivateField(chessModel,"canCastleQueenSideWhite", Boolean::class.java))
    }

    @Test
    fun `white king and queenside rook on starting positions - castling queen side is true`() {
        val chessModel = ChessModel()
        setPrivateField(chessModel, "pieces", mutableListOf(
            ChessPiece(7,4,R.drawable.white_king),
            ChessPiece(7,0,R.drawable.white_rook)))
        invokePrivateMethod(chessModel,"setCastlingRightsWhite")

        assertFalse(getPrivateField(chessModel,"canCastleKingSideWhite", Boolean::class.java))
        assertTrue(getPrivateField(chessModel,"canCastleQueenSideWhite", Boolean::class.java))
    }

    @Test
    fun `white king and kingside rook on starting positions - castling king side is true`() {
        val chessModel = ChessModel()
        setPrivateField(chessModel, "pieces", mutableListOf(
            ChessPiece(7,4,R.drawable.white_king),
            ChessPiece(7,7,R.drawable.white_rook)))
        invokePrivateMethod(chessModel,"setCastlingRightsWhite")

        assertTrue(getPrivateField(chessModel,"canCastleKingSideWhite", Boolean::class.java))
        assertFalse(getPrivateField(chessModel,"canCastleQueenSideWhite", Boolean::class.java))
    }

    @Test
    fun `white king and both rooks on starting positions - castling white side are true`() {
        val chessModel = ChessModel()
        setPrivateField(chessModel, "pieces", mutableListOf(
            ChessPiece(7,4,R.drawable.white_king),
            ChessPiece(7,0,R.drawable.white_rook),
            ChessPiece(7,7,R.drawable.white_rook)))
        invokePrivateMethod(chessModel,"setCastlingRightsWhite")

        assertTrue(getPrivateField(chessModel,"canCastleKingSideWhite", Boolean::class.java))
        assertTrue(getPrivateField(chessModel,"canCastleQueenSideWhite", Boolean::class.java))
    }
}