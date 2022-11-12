package chess.rishi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Board extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new Board();
    }

    Boxes[][] sqr = new Boxes[8][8];

    Piece temp;
    Piece[] pieces = new Piece[12];

    JPanel p, pro;
    JButton[] pr = new JButton[4];
    JButton re = new JButton();

    boolean grab = true, move = true, promo = false, End = false, moved = false;
    int[][] mov;
    int x, y, r, s, g, h, h1, WCount = 16, BCount = 16, b;
    ArrayList<Integer[]> cMovable = new ArrayList<>();


    Board() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                    File file = new File("positions.txt");
                    //noinspection ResultOfMethodCallIgnored
                    file.delete();
                System.exit(0);
            }
        });

        pieces[0]  = new Piece("Pawn",   false, new ImageIcon("Pieces/Chess_pdt60.png"));//BLACK
        pieces[1]  = new Piece("Pawn",   true,  new ImageIcon("Pieces/Chess_plt60.png"));//WHITE
        pieces[2]  = new Piece("King",   false, new ImageIcon("Pieces/Chess_kdt60.png"));//BLACK
        pieces[3]  = new Piece("King",   true,  new ImageIcon("Pieces/Chess_klt60.png"));//WHITE
        pieces[4]  = new Piece("Queen",  false, new ImageIcon("Pieces/Chess_qdt60.png"));
        pieces[5]  = new Piece("Queen",  true,  new ImageIcon("Pieces/Chess_qlt60.png"));
        pieces[6]  = new Piece("Rook",   false, new ImageIcon("Pieces/Chess_rdt60.png"));
        pieces[7]  = new Piece("Rook",   true,  new ImageIcon("Pieces/Chess_rlt60.png"));
        pieces[8]  = new Piece("Knight", false, new ImageIcon("Pieces/Chess_ndt60.png"));
        pieces[9]  = new Piece("Knight", true,  new ImageIcon("Pieces/Chess_nlt60.png"));
        pieces[10] = new Piece("Bishop", false, new ImageIcon("Pieces/Chess_bdt60.png"));
        pieces[11] = new Piece("Bishop", true,  new ImageIcon("Pieces/Chess_blt60.png"));

        this.setSize(700, 730);
        this.setLayout(null);
        this.setResizable(false);

        p = new JPanel();
        p.setBounds(50, 50, 600, 600);
        p.setLayout(null);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sqr[i][j] = new Boxes();
                sqr[i][j].b.setLocation(j * 75, i * 75);
                sqr[i][j].b.addActionListener(this);
                sqr[i][j].b.setFocusable(false);
                resetColors(i,j);
                p.add(sqr[i][j].b);
            }
        }
        File file = new File("positions.txt");
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //NEW BOARD -------
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                sqr[(i * 5) + 1][j].setBox(true, pieces[i]);
            }

            for (int j = 0; j < 2; j++) {
                sqr[i * 7][j * 7].setBox(true, pieces[6 + i]);
                sqr[i * 7][Math.abs((j * 7) - 1)].setBox(true, pieces[8 + i]);
                sqr[i * 7][Math.abs((j * 7) - 2)].setBox(true, pieces[10 + i]);
            }
            sqr[i * 7][3].setBox(true, pieces[4 + i]);
            sqr[i * 7][4].setBox(true, pieces[2 + i]);

        }
        //--------

        this.add(p);
        this.setVisible(true);
    }

    public void resetColors(int i, int j) {
        if ((i + j) % 2 == 0) {
            sqr[i][j].b.setBackground(new Color(214, 193, 138));
        } else {
            sqr[i][j].b.setBackground(new Color(144, 74, 38));
        }
    }

    public void setMColours(int m, int n) {
        if ((m + n) % 2 == 0) {
            sqr[m][n].b.setBackground(new Color(178, 201, 115));
        } else {
            sqr[m][n].b.setBackground(new Color(96, 129, 25));
        }
    }

    public static int[][] conInt(ArrayList<Integer[]> integers) {

        int[][] ret = new int[integers.size()][2];

        for (int i = 0; i < ret.length; i++) {
            Integer[] x = integers.get(i);
            for (int j = 0; j < 2; j++) {
                ret[i][j] = x[j];
            }
        }
        return ret;
    }

    public boolean setReset(int a, int b, int c, int d) {

        boolean C, bool = false;
        if (sqr[c][d].IsPiece) {
            bool = true;
            temp = sqr[c][d].piece;
        }
        sqr[c][d].setBox(true, sqr[a][b].piece);
        sqr[a][b].clearBox();
        Check(false, false);
        C = move?(!Boxes.WCheck):(!Boxes.BCheck);
        sqr[a][b].setBox(true, sqr[c][d].piece);
        if (bool) {
            sqr[c][d].setBox(true, temp);
        } else sqr[c][d].clearBox();
        return C;

    }

    public ArrayList<Integer[]> getCheckMove(int i, int j) {

        ArrayList<Integer[]> cm = new ArrayList<>();
        int[][] gMov = conInt(sqr[i][j].piece.getMovable(i, j, sqr));

        // ----- En-Passant -----
        if (Boxes.EnPassant && x == g && y == h) {
            Piece t;
            if (sqr[i][j].piece.color) {
                t = sqr[g][h1].piece;
                sqr[g][h1].clearBox();
                if (setReset(i, j, i - 1, h1)) {
                    cm.add(new Integer[]{i - 1, h1});
                    if (!grab) setMColours(i - 1, h1);
                }
                sqr[g][h1].setBox(true, t);
            } else {
                t = sqr[g][h1].piece;
                sqr[g][h1].clearBox();
                if (setReset(i, j, i + 1, h1)) {
                    cm.add(new Integer[]{i + 1, h1});
                    if (!grab) setMColours(i + 1, h1);
                }
                sqr[g][h1].setBox(true, t);
            }
        }
        // ----- En-Passant -----

        for (int[] k : gMov) {
            if (setReset(i, j, k[0], k[1])) {
                cm.add(new Integer[]{k[0], k[1]});
                if (!grab) setMColours(k[0], k[1]);
            }
            temp = null;
        }
        return cm;
    }

    public void Check(boolean KBool, boolean cColour) {

        boolean c1 = false, noCheck = true;
        for (int p = 0; p < 8; p++) {
            for (int q = 0; q < 8; q++) {
                if (sqr[p][q].piece == null) continue;
                if (sqr[p][q].piece.color == !move) {
                    int[][] d = conInt(sqr[p][q].piece.getMovable(p, q, sqr));
                    for (int[] f : d) {
                        if (sqr[f[0]][f[1]].piece != null) {
                            if (sqr[f[0]][f[1]].piece.name.equals("King") && (move == sqr[f[0]][f[1]].piece.color)) {
                                if (KBool) {
                                    r = f[0];
                                    s = f[1];
                                }
                                noCheck = false;
                                if (move) {
                                    Boxes.WCheck = true;
                                } else {
                                    Boxes.BCheck = true;
                                }
                                if (cColour) sqr[f[0]][f[1]].b.setBackground(new Color(225, 95, 95));
                                c1 = true;
                                break;
                            }
                        }
                    }
                    if (noCheck) {
                        Boxes.WCheck = false;
                        Boxes.BCheck = false;
                    }
                }
                if (c1) break;
            }
            if (c1) break;
        }
    }

    public void CheckMate() {
        int e, f;
        if (move) {
            e = Boxes.wr;
            f = Boxes.ws;
        } else {
            e = Boxes.br;
            f = Boxes.bs;
        }
        int count = 0;
        for (int p = 0; p < 8; p++) {
            for (int q = 0; q < 8; q++) {
                if (sqr[p][q].IsPiece) {
                    if (sqr[p][q].piece.color == move) {
                        if (p == e && q == f) continue;
                        if (conInt(getCheckMove(p, q)).length == 0) {
                            count++;
                        }
                    }
                }
            }
        }
        if (move) {
            if (count == WCount - 1) {
                End = true;
                if (Boxes.WCheck) {
                    End('C');
                } else {
                    End('S');
                }
            }
        } else {
            if (count == BCount - 1) {
                End = true;
                if (Boxes.BCheck) {
                    End('c');
                } else {
                    End('s');
                }
            }
        }
    }

    public void Promotion(boolean color) {

        int p = 4;
        if (color) b = 1;
        else b = 0;

        this.setSize(new Dimension(700, 810));
        pro = new JPanel();
        pro.setBounds(192, 675, 305, 80);
        pro.setBackground(Color.lightGray);
        pro.setLayout(null);

        for (int a = 0; a < 4; a++) {
            pr[a] = new JButton();
            pr[a].setBackground(Color.GRAY);
            pr[a].setBounds((a * 70) + ((a + 1) * 5), 5, 70, 70);
            pr[a].setFocusable(false);
            pr[a].setIcon(pieces[p + b].i);
            pr[a].addActionListener(this);
            p += 2;
            pro.add(pr[a]);
        }

        this.add(pro);
        promo = true;
    }

    public void flip() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (move){
                    sqr[i][j].b.setLocation(j * 75, i * 75);
                }
                else {
                    sqr[i][j].b.setLocation(Math.abs(j-7) * 75, Math.abs(i-7) * 75);
                }
            }
        }
    }

    public void End(char c){
        JTextField a = new JTextField(), b = new JTextField();
        pro = new JPanel();
        String s = null,str = null;

        this.setSize(new Dimension(700, 840));
        pro.setBounds(192, 675, 305, 110);
        pro.setBackground(new Color(215,215,215));
        pro.setLayout(null);

        a.setBounds(50,10,200,30);
        a.setLayout(null);
        a.setBackground(new Color(215,215,215));
        a.setHorizontalAlignment(JTextField.CENTER);
        b.setFont(new Font("Monospaced",Font.BOLD,16));
        a.setBorder(null);
        a.setEditable(false);

        b.setBounds(50,40,200,30);
        b.setLayout(null);
        b.setBackground(new Color(215,215,215));
        b.setHorizontalAlignment(JTextField.CENTER);
        b.setFont(new Font("Monospaced",Font.PLAIN,12));
        b.setBorder(null);
        b.setEditable(false);

        re.setBounds(115,80,75,20);
        re.setBackground(new Color(200,200,200));
        re.addActionListener(this);
        re.setFont(new Font("Berlin Sans",Font.PLAIN,10));
        re.setText("Rematch");


        switch (c) {
            case 'C' -> { str = "CHECKMATE!"; s = "BLACK Wins"; }
            case 'c' -> { str = "CHECKMATE!"; s = "WHITE Wins"; }
            case 'S' -> { str = "DRAW"; s = "BLACK Was Stalemated"; }
            case 's' -> { str = "DRAW"; s = "WHITE Was Stalemated"; }
            case 'I' -> { str = "DRAW"; s = "Draw by Insufficient Material"; }
            case 'R' -> { str = "DRAW"; s = "Draw by Repetition"; }
        }
        a.setText(str);
        b.setText(s);

        pro.add(re);
        pro.add(a);
        pro.add(b);

        this.add(pro);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (promo) {
            for (int i = 0; i < 4; i++) {
                if (pr[i] == e.getSource()) {
                    i += 1;
                    sqr[x][y].setBox(true, pieces[(i * 2) + 2 + b]);
                    this.remove(pro);
                    this.setSize(700, 730);
                    promo = false;
                    Check(true, true);
                    if (Boxes.WCheck || Boxes.BCheck) {
                        if (conInt(getCheckMove(r, s)).length == 0) {
                            CheckMate();
                        }
                    }
                }
            }
        }
        else if (End) {
            if (re == e.getSource()){
                this.dispose();
                File file = new File("positions.txt");
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                new Board();
            }
        }
        else {
            boolean c = false;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (sqr[i][j].b == e.getSource()) {
                        if (!sqr[i][j].IsPiece && grab) {
                            moved = false;
                            c = true;
                            break;
                        }
                        if (sqr[i][j].IsPiece && grab) {
                            if (move != sqr[i][j].piece.color) {
                                moved = false;
                                c = true;
                                break;
                            }
                        }
                        if (grab) {
                            moved = false;
                            x = i;
                            y = j;
                            c = true;
                            grab = false;
                            cMovable = getCheckMove(i, j);
                            break;
                        }
                        mov = conInt(cMovable);
                        cMovable.clear();
                        if (i == x && j == y) {
                            for (int[] k : mov) {
                                resetColors(k[0], k[1]);
                            }
                            grab = true;
                            c = true;
                            break;
                        }
                        if (sqr[i][j].IsPiece) {
                            if ((sqr[i][j].piece.color == sqr[x][y].piece.color)) {
                                for (int[] k : mov) {
                                    resetColors(k[0], k[1]);
                                }
                                grab = true;
                                c = true;
                                break;
                            }
                        }
                        for (int[] k : mov) {
                            if (i == k[0] && j == k[1]) {
                                moved = true;
                                boolean castling = false;

                                // ----- En-Passant -----
                                if (Boxes.EnPassant) {
                                    if (x == g && y == h) {
                                        if (j == y - 1) {
                                            sqr[x][y - 1].clearBox();
                                        } else if (j == y + 1) {
                                            sqr[x][y + 1].clearBox();
                                        }
                                        if (!move) WCount -= 1;
                                        else BCount -= 1;
                                    }
                                    Boxes.EnPassant = false;
                                } else if (sqr[x][y].piece.name.equals("Pawn")) {
                                    if (sqr[x][y].piece.color) {
                                        if (i == x - 2) {
                                            if (j + 1 < 8) if (sqr[i][j + 1].IsPiece)
                                                if (!sqr[i][j + 1].piece.color && sqr[i][j + 1].piece.name.equals("Pawn")) {
                                                    Boxes.EnPassant = true;
                                                    g = i;
                                                    h = j + 1;
                                                    h1 = j;
                                                }
                                            if (j - 1 >= 0) if (sqr[i][j - 1].IsPiece)
                                                if (!sqr[i][j - 1].piece.color && sqr[i][j - 1].piece.name.equals("Pawn")) {
                                                    Boxes.EnPassant = true;
                                                    g = i;
                                                    h = j - 1;
                                                    h1 = j;
                                                }
                                        }
                                    } else {
                                        if (i == x + 2) {
                                            if (j + 1 < 8) if (sqr[i][j + 1].IsPiece)
                                                if (sqr[i][j + 1].piece.color && sqr[i][j + 1].piece.name.equals("Pawn")) {
                                                    Boxes.EnPassant = true;
                                                    g = i;
                                                    h = j + 1;
                                                    h1 = j;
                                                }
                                            if (j - 1 >= 0) if (sqr[i][j - 1].IsPiece)
                                                if (sqr[i][j - 1].piece.color && sqr[i][j - 1].piece.name.equals("Pawn")) {
                                                    Boxes.EnPassant = true;
                                                    g = i;
                                                    h = j - 1;
                                                    h1 = j;
                                                }
                                        }
                                    }
                                }

                                // ----- En-Passant -----
                                if (Boxes.WRC || Boxes.WLC || Boxes.BRC || Boxes.BLC) {
                                    if (sqr[x][y].piece.name.equals("King")) {
                                        if (j != y + 2 && j != y - 3) {
                                            if (sqr[x][y].piece.color) {
                                                Boxes.WRC = false;
                                                Boxes.WLC = false;
                                            } else {
                                                Boxes.BRC = false;
                                                Boxes.BLC = false;
                                            }
                                        } else {
                                            boolean b1, b2;
                                            if (sqr[x][y].piece.color) {
                                                b1 = Boxes.WRC;
                                                b2 = Boxes.WLC;
                                            } else {
                                                b1 = Boxes.BRC;
                                                b2 = Boxes.BLC;
                                            }
                                            if (b1) {
                                                if (j == y + 2) {
                                                    sqr[x][y + 2].setBox(true, sqr[x][y].piece);
                                                    sqr[x][y + 1].setBox(true, sqr[x][y + 3].piece);
                                                    sqr[x][y].clearBox();
                                                    sqr[x][y + 3].clearBox();
                                                    if (sqr[x][y + 2].piece.color) {
                                                        Boxes.wr = i;
                                                        Boxes.ws = j;
                                                        Boxes.WRC = false;
                                                        Boxes.WLC = false;
                                                    } else {
                                                        Boxes.br = i;
                                                        Boxes.bs = j;
                                                        Boxes.BRC = false;
                                                        Boxes.BLC = false;
                                                    }
                                                    castling = true;
                                                }
                                            } else if (b2) {
                                                if (j == y - 3) {
                                                    sqr[x][y - 3].setBox(true, sqr[x][y].piece);
                                                    sqr[x][y - 2].setBox(true, sqr[x][y - 4].piece);
                                                    sqr[x][y].clearBox();
                                                    sqr[x][y - 4].clearBox();
                                                    if (sqr[x][y - 3].piece.color) {
                                                        Boxes.wr = i;
                                                        Boxes.ws = j;
                                                        Boxes.WRC = false;
                                                        Boxes.WLC = false;
                                                    } else {
                                                        Boxes.br = i;
                                                        Boxes.bs = j;
                                                        Boxes.BRC = false;
                                                        Boxes.BLC = false;
                                                    }
                                                    castling = true;
                                                }
                                            }
                                        }
                                    } else if (sqr[x][y].piece.name.equals("Rook")) {
                                        if (sqr[x][y].piece.color) {
                                            if (y == 0) Boxes.WLC = false;
                                            else Boxes.WRC = false;
                                        } else {
                                            if (y == 0) Boxes.BLC = false;
                                            else Boxes.BRC = false;
                                        }
                                    }
                                }
                                if (!castling) {
                                    if (sqr[i][j].IsPiece) {
                                        if ((sqr[i][j].piece.color != sqr[x][y].piece.color)) {
                                            if (!move) {
                                                WCount -= 1;
                                            } else {
                                                BCount -= 1;
                                            }
                                        }
                                    }
                                    sqr[i][j].setBox(true, sqr[x][y].piece);
                                    sqr[x][y].clearBox();
                                    if (sqr[i][j].piece.name.equals("King")) {
                                        if (sqr[i][j].piece.color) {
                                            Boxes.wr = i;
                                            Boxes.ws = j;
                                        } else {
                                            Boxes.br = i;
                                            Boxes.bs = j;
                                        }
                                    }
                                    if (sqr[i][j].piece.name.equals("Pawn") && (i == 0 || i == 7)) {
                                        if (sqr[i][j].piece.color) {
                                            if (i == 0) {
                                                Promotion(true);
                                                x = i;
                                                y = j;
                                            }
                                        } else if (i == 7) {
                                            Promotion(false);
                                            x = i;
                                            y = j;
                                        }
                                    }
                                }
                                grab = true;
                                move = !move;
                                if (move) {
                                    if (conInt(getCheckMove(Boxes.wr, Boxes.ws)).length == 0) {
                                        CheckMate();
                                    }
                                } else {
                                    if (conInt(getCheckMove(Boxes.br, Boxes.bs)).length == 0) {
                                        CheckMate();
                                    }
                                }
                                break;
                            }
                        }
                        Check(true, true);
                        grab = true;
                        for (int[] k : mov) {
                            if (!(Boxes.WCheck || Boxes.BCheck)) resetColors(r, s);
                            resetColors(k[0], k[1]);
                        }
                        c = true;

                        if (WCount == 1 && BCount == 1) {
                            End = true;
                            End('I');
                        }
                        break;
                    }
                }
                if (c) break;
            }
        }
        if (moved) {
            flip();
            moved = false;
            try {
                FileWriter writer = new FileWriter("positions.txt", true);
                StringBuilder str = new StringBuilder();
                if (move) str.append("0");
                else str.append("1");

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (sqr[i][j].IsPiece) {
                            if (sqr[i][j].piece.color) {
                                switch (sqr[i][j].piece.name) {
                                    case "King"   -> str.append("1");
                                    case "Queen"  -> str.append("2");
                                    case "Knight" -> str.append("3");
                                    case "Bishop" -> str.append("4");
                                    case "Rook"   -> str.append("5");
                                    case "Pawn"   -> str.append("6");
                                }
                            }
                            else {
                                switch (sqr[i][j].piece.name) {
                                    case "King"   -> str.append("A");
                                    case "Queen"  -> str.append("B");
                                    case "Knight" -> str.append("C");
                                    case "Bishop" -> str.append("D");
                                    case "Rook"   -> str.append("E");
                                    case "Pawn"   -> str.append("F");
                                }
                            }
                        } else str.append("0");
                    }
                }
                if (Boxes.EnPassant) str.append("1");
                else str.append("0");

                if (Boxes.WRC || Boxes.WLC || Boxes.BLC || Boxes.BRC) {
                    if (Boxes.WRC) {
                        if (!sqr[7][5].IsPiece && !sqr[7][6].IsPiece) {
                            str.append("1");
                        } else str.append("0");
                    }
                    if (Boxes.BRC) {
                        if (!sqr[0][5].IsPiece && !sqr[0][6].IsPiece) {
                            str.append("1");
                        } else str.append("0");
                    }
                    if (Boxes.WLC) {
                        if (!sqr[7][3].IsPiece && !sqr[7][2].IsPiece && !sqr[7][1].IsPiece) {
                            str.append("1");
                        } else str.append("0");
                    } else {
                        if (!sqr[0][3].IsPiece && !sqr[0][2].IsPiece && !sqr[0][1].IsPiece) {
                            str.append("1");
                        } else str.append("0");
                    }
                } else str.append("0000");
                writer.append(String.valueOf(str));
                writer.append("\n");
                FileInputStream file = new FileInputStream("positions.txt");
                Scanner scanner = new Scanner(file);

                int count = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.equals(String.valueOf(str))) {
                        count++;
                    }
                }
                if (count == 2) {
                    End('R');
                    End = true;
                }
                scanner.close();
                file.close();
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}