package chess.rishi;

import javax.swing.*;
import java.util.ArrayList;

public class Piece{

    String name;
    boolean color;
    ImageIcon i;
    ArrayList<Integer[]> movable = new ArrayList<>();

    public Piece(String name, boolean color, ImageIcon i) {
        this.name = name;
        this.color = color;
        this.i = i;
    }

    public ArrayList<Integer[]> getMovable(int i, int j, Boxes[][] sqr) {

        this.movable.clear();
        //noinspection IfCanBeSwitch
        if (this.name.equals("Pawn")){
            if (color){
                if(i==6) {
                    if (!sqr[i - 2][j].IsPiece && !sqr[i - 1][j].IsPiece) this.movable.add(new Integer[]{i - 2, j});
                }
                if (i-1>=0) {
                    if (!sqr[i-1][j].IsPiece) this.movable.add(new Integer[]{i-1,j});
                    if (j+1<8)  if (sqr[i-1][j+1].IsPiece) if (!sqr[i-1][j+1].piece.color) this.movable.add(new Integer[]{i-1,j+1});
                    if (j-1>=0) if (sqr[i-1][j-1].IsPiece) if (!sqr[i-1][j-1].piece.color) this.movable.add(new Integer[]{i-1,j-1});
                }
            }
            else {
                if(i==1) {
                    if (!sqr[i + 2][j].IsPiece && !sqr[i + 1][j].IsPiece) this.movable.add(new Integer[]{i + 2, j});
                }
                if (i+1<8) {
                    if (!sqr[i+1][j].IsPiece) this.movable.add(new Integer[]{i+1,j});
                    if (j+1<8)  if (sqr[i+1][j+1].IsPiece) if (sqr[i+1][j+1].piece.color) this.movable.add(new Integer[]{i+1,j+1});
                    if (j-1>=0) if (sqr[i+1][j-1].IsPiece) if (sqr[i+1][j-1].piece.color) this.movable.add(new Integer[]{i+1,j-1});
                }
            }
        }

        else if(this.name.equals("Knight")) {
            int[][] x = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            for (int[] a : x){
                int k = a[0]+i, l = a[1]+j;
                if (k<8 && k>=0 && l<8 && l>=0) {
                    if(sqr[k][l].IsPiece){
                        if (this.color != sqr[k][l].piece.color) {
                            this.movable.add(new Integer[]{k ,l});
                        }
                    }
                    else {
                        this.movable.add(new Integer[]{k ,l});
                    }
                }
            }
        }

        else if(this.name.equals("Bishop")) {
            int xc = 0;
            for (int x = 1+i; xc<2; x = -1+i) {
                int yc = 0;
                for (int y = 1+j; yc<2 ; y = -1+j) {
                    while (x < 8 && x >= 0 && y < 8 && y >= 0) {
                        if (sqr[x][y].IsPiece) {
                            if (this.color != sqr[x][y].piece.color) {
                                this.movable.add(new Integer[]{x, y});
                            }
                            break;
                        } else {
                            this.movable.add(new Integer[]{x, y});
                        }
                        if (xc==0){x++;} else {x--;}
                        if (yc==0){y++;} else {y--;}
                    }
                    yc++;
                    if(yc==1) {
                        if(xc==1) x=-1+i; else x=1+i;
                    }
                }
                xc++;
            }
        }

        else if(this.name.equals("Rook")) {
            int xc = 0, yc = 0;
            for (int x =1+i; xc<2; x =-1+i) {
                while (x<8 && x>=0){
                    if(sqr[x][j].IsPiece){
                        if (this.color != sqr[x][j].piece.color) {
                            this.movable.add(new Integer[]{x, j});
                        }
                        break;
                    } else {
                        this.movable.add(new Integer[]{x, j});
                    }
                    if (xc==0) {x++;} else {x--;}
                }
                xc++;
            }
            for (int y =1+j; yc<2; y =-1+j) {
                while (y<8 && y>=0){
                    if(sqr[i][y].IsPiece){
                        if (this.color != sqr[i][y].piece.color) {
                            this.movable.add(new Integer[]{i, y});
                        }
                        break;
                    } else {
                        this.movable.add(new Integer[]{i, y});
                    }
                    if (yc==0){y++;} else {y--;}
                }
                yc++;
            }
        }

        else if(this.name.equals("Queen")) {
            Piece p1 = new Piece("Bishop", this.color, null);
            Piece p2 = new Piece("Rook",   this.color, null);
            this.movable.addAll(p1.getMovable(i, j, sqr));
            this.movable.addAll(p2.getMovable(i, j, sqr));
        }

        else {  // KING
            int[] x = {0, 1, -1};
            for (int k : x) {
                k += i;
                for (int l : x) {
                    l += j;
                    if (k < 8 && k >= 0 && l < 8 && l >= 0) {
                        if (sqr[k][l].IsPiece) {
                            if (this.color != sqr[k][l].piece.color) {
                                this.movable.add(new Integer[]{k, l});
                            }
                        } else {
                            this.movable.add(new Integer[]{k, l});
                        }
                    }
                }
            }
            if (Boxes.WRC || Boxes.WLC || Boxes.BRC || Boxes.BLC) {
                if (!Boxes.WCheck && !Boxes.BCheck) {
                    boolean b1, b2;
                    if (this.color) {
                        b1 = Boxes.WRC;
                        b2 = Boxes.WLC;
                    } else {
                        b1 = Boxes.BRC;
                        b2 = Boxes.BLC;
                    }
                    if (b1) {
                        if (!sqr[i][j + 1].IsPiece && !sqr[i][j + 2].IsPiece) {
                            this.movable.add(new Integer[]{i, j + 2});
                        }
                    }
                    if (b2) {
                        if (!sqr[i][j - 1].IsPiece && !sqr[i][j - 2].IsPiece && !sqr[i][j - 3].IsPiece) {
                            this.movable.add(new Integer[]{i, j - 3});
                        }
                    }
                }
            }
        }
        return this.movable;
    }
}