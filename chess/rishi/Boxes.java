package chess.rishi;

import javax.swing.*;

public class Boxes {

    boolean IsPiece;
    Piece piece;
    JButton b = new JButton();
    static int wr = 7, ws = 4, br = 0, bs = 4;
    static boolean WCheck = false, BCheck = false, EnPassant = false, WRC = true, BRC = true, WLC = true, BLC = true;

    public Boxes() {
        this.IsPiece = false;
        this.piece = null;
        this.b.setSize(75, 75);
    }

    public void setBox(boolean isPiece, Piece piece) {
        this.IsPiece = isPiece;
        this.piece = piece;
        if (piece != null) {
            this.b.setIcon(piece.i);
        }
    }

    public void clearBox() {
        this.IsPiece = false;
        this.piece = null;
        this.b.setIcon(null);
    }
}