package game2048;

import java.util.Formatter;
import java.util.Observable;


/**
 * The state of a game of 2048.
 *
 * @author: zzx
 */
public class Model extends Observable {
    /**
     * Current contents of the board.
     */
    private Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;
    /**
     * True iff game is ended.
     */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */

    //面向过程思想 :子任务划分法
    // 1. 完成向上的任务
    // 2. 完成一列向上的任务
    // 3. 对应一个具体的格子怎么做?
    //      a. 若格子上面没有其他的格子就移动到顶格
    //      b. 若有格子上面有格子
    //          i. 格子和上面的格子值不一样, 移动到下一个格子即可
    //          ii.格子的值和上面格子值一样 - 没有合并过,怎么处理 - 合并过,怎么处理
    // 通用的查找下一个 Tile 方法
    private Tile getNextUpTile(int col, int row) {
        if (row == board.size() - 1) {
            return null;
        }
        row += 1;
        while (row < board.size()) {
            Tile tile = board.tile(col, row);
            if (tile != null) {
                return tile;
            }
            row++;
        }
        return null;
    }

    private boolean tiltNorth() {
        boolean changed = false;
        for (int c = 0; c < board.size(); c++) {
            boolean[] isMerged = new boolean[board.size()];
            for (int r = board.size() - 1; r >= 0; --r) {
                Tile tile = board.tile(c, r);
                if (tile == null) continue;
                Tile up = getNextUpTile(c, r);
                if (up == null) {
                    if (r == board.size() - 1) continue;
                    board.move(c, 3, tile);
                    changed = true;
                } else {
                    if (up.value() == tile.value() && !isMerged[up.row()]) {
                        isMerged[up.row()] = true;
                        board.move(c, up.row(), tile);
                        changed = true;
                        score += tile.value()*2;
                    } else {
                        if (r == up.row() - 1) continue;
                        board.move(c, up.row() - 1, tile);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private Tile getNextLeftTile(int row, int col) {
        if (col == 0) {
            return null;
        }
        col -= 1;
        while (col >= 0) {
            Tile tile = board.tile(col, row);
            if (tile != null) {
                return tile;
            }
            col--;
        }
        return null;
    }


    private boolean tiltWest() {
        boolean changed = false;
        for (int r = 0; r < board.size(); r++) {
            boolean[] isMerged = new boolean[board.size()];
            for (int c = 0; c < board.size(); c++) {
                Tile tile = board.tile(c, r);
                if (tile == null) continue;
                Tile left = getNextLeftTile(r, c);
                if (left == null) {
                    if (c == 0) continue;
                    board.move(0, r, tile);
                    changed = true;
                } else {
                    if (left.value() == tile.value() && !isMerged[left.col()]) {
                        isMerged[left.col()] = true;
                        board.move(left.col(), r, tile);
                        changed = true;
                        score += tile.value()*2;
                    } else {
                        if (c == left.col() + 1) continue;
                        board.move(left.col() + 1, r, tile);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private Tile getNextDownTile(int col, int row) {
        if (row == 0) {
            return null;
        }
        row -= 1;
        while (row >= 0) {
            Tile tile = board.tile(col, row);
            if (tile != null) {
                return tile;
            }
            row--;
        }
        return null;
    }

    private boolean tiltSouth() {
        boolean changed = false;

        for (int c = 0; c < board.size(); c++) {
            boolean[] isMerged = new boolean[board.size()];
            for (int r = 0; r < board.size(); r++) {
                Tile tile = board.tile(c, r);
                if (tile == null) continue;
                Tile down = getNextDownTile(c, r);
                if (down == null) {
                    if (r == 0) continue;
                    board.move(c, 0, tile);
                    changed = true;
                } else {
                    if (down.value() == tile.value() && !isMerged[down.row()]) {
                        isMerged[down.row()] = true;
                        board.move(c, down.row(), tile);
                        changed = true;
                        score += tile.value()*2;
                    } else {
                        if (r == down.row() + 1) continue;
                        board.move(c, down.row() + 1, tile);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }


    private Tile getNextRightTile(int row, int col) {
        if (col == board.size() - 1) {
            return null;
        }
        col += 1;
        while (col < board.size()) {
            Tile tile = board.tile(col, row);
            if (tile != null) {
                return tile;
            }
            col++;
        }
        return null;
    }

    private boolean tiltEast() {
        boolean changed = false;

        for (int r = 0; r < board.size(); r++) {
            boolean[] isMerged = new boolean[board.size()];
            for (int c = board.size() - 1; c >= 0; c--) {
                Tile tile = board.tile(c, r);
                if (tile == null) continue;
                Tile right = getNextRightTile(r, c);
                if (right == null) {
                    if (c == board.size() - 1) continue;
                    board.move(board.size() - 1, r, tile);
                    changed = true;
                } else {
                    if (right.value() == tile.value() && !isMerged[right.col()]) {
                        isMerged[right.col()] = true;
                        board.move(right.col(), r, tile);
                        changed = true;
                        score += tile.value()*2;
                    } else {
                        if (c == right.col() - 1) continue;
                        board.move(right.col() - 1, r, tile);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }


    public boolean tilt(Side side) {
        boolean changed = false;
        switch (side) {
            case NORTH:
                changed = tiltNorth();
                break;
            case SOUTH:
                changed = tiltSouth();
                break;
            case EAST:
                changed = tiltEast();
                break;
            case WEST:
                changed = tiltWest();
                break;

        }
        if (changed) setChanged();
        return changed;
    }


    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /**
     * Determine whether game is over.
     */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); ++i) {
            for (int j = 0; j < b.size(); ++j) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */


    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); ++i) {
            for (int j = 0; j < b.size(); ++j) {
                if (b.tile(i, j) == null) continue;
                if (b.tile(i, j).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int[] dir = {-1, 0, 1, 0, -1};
        //判断1 : 存在一个空位
        if (emptySpaceExists(b)) {
            return true;
        }
        // 判断2 : 存在可以抵消的块(tile)
        for (int i = 0; i < b.size(); ++i) {
            for (int j = 0; j < b.size(); ++j) {
                Tile tile = b.tile(i, j);
                for (int k = 0; k < 4; ++k) {
                    int dx = i + dir[k], dy = j + dir[k + 1];
                    if (dx < 0 || dx >= b.size() || dy < 0 || dy >= b.size()) continue;
                    Tile tile1 = b.tile(dx, dy);
                    if (tile1.value() == tile.value()) return true;
                }
            }
        }
        return false;
    }


    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
