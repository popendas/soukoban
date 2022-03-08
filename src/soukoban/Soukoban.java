package soukoban;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BooleanSupplier;

/**
 * 倉庫番を遊ぶためのクラス
 */
public class Soukoban {
    private Area[][] defaultStage;
    private int playerX, playerY; //プレイヤーの現在地を示す
    private int clearCnt = 0;//目的地の数を示す
    private Area[][] stage;
    private final List<BooleanSupplier> LOG = new LinkedList<>();
    private final BooleanSupplier down = () -> move(0, 1);
    private final BooleanSupplier left = () -> move(-1, 0);
    private final BooleanSupplier right = () -> move(1, 0);
    private final BooleanSupplier up = () -> move(0, -1);

    public Soukoban(int[][] defaultStage) {
        setStage(defaultStage);
    }

    public Soukoban() {
    }

    public void moveDown() {
        move(down);
    }

    public void moveLeft() {
        move(left);

    }

    public void moveRight() {
        move(right);
    }

    public void moveUp() {
        move(up);
    }

    public void back() {
        if (!LOG.isEmpty()) {
            LOG.remove(LOG.size() - 1);
            stageReset();
            LOG.forEach(BooleanSupplier::getAsBoolean);
        }
    }

    public void reset() {
        stageReset();
        LOG.clear();
    }

    /**
     * フィールドの目的地すべてに箱を設置できていたらtrue、それ以外はfalseを返します。
     *
     * @return 目的地すべてに箱を設置できていたらtrue、それ以外はfalse
     */
    public boolean isClear() {
        return clearCnt <= 0;
    }

    public Area[][] getStage() {
        return stage;
    }

    public int getMoveCount() {
        return LOG.size();
    }

    public void setStage(int[][] defaultStage) {
        this.defaultStage = new Area[defaultStage.length][];
        //int配列をArea配列に変換
        for (int i = 0; i < defaultStage.length; i++) {
            Area[] line = new Area[defaultStage[i].length];
            for (int j = 0; j < line.length; j++) {
                line[j] = Area.getById(defaultStage[i][j]);
            }
            this.defaultStage[i] = line;
        }

        this.stage = new Area[this.defaultStage.length][];
        reset();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("\n");
        for (Area[] areaArray : stage) {
            StringBuilder sb = new StringBuilder(areaArray.length + 2);
            for (Area area : areaArray) {
                sb.append(area.getLabel());
            }
            sj.add(sb);
        }
        return sj.toString();
    }

    private void stageReset() {
        for (int i = 0; i < defaultStage.length; i++) {
            stage[i] = defaultStage[i].clone();
        }
        playerSearch();
        countClear();
    }

    private void countClear() {
        clearCnt = 0;
        for (Area[] areaArray : defaultStage) { //目的地の数をカウント
            for (Area area : areaArray) {
                if (area == Area.CLEAR_POINT || area == Area.MIX_MARK) {
                    clearCnt++;
                }
            }
        }
    }

    private void playerSearch() {
        for (int i = 0; i < stage.length; i++) {
            for (int j = 0; j < stage[i].length; j++) {
                if (stage[i][j] == Area.PLAYER || stage[i][j] == Area.MIX_MARK) {
                    playerX = j;
                    playerY = i;
                    return;
                }
            }
        }
        throw new IllegalArgumentException("プレイヤーが設置されていません");
    }

    private void move(BooleanSupplier bSupp) {
        if (bSupp.getAsBoolean())
            LOG.add(bSupp);
    }

    private boolean move(final int X, final int Y) {
        try {
            moveBox(X, Y);
            return movePlayer(X, Y);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private boolean movePlayer(final int X, final int Y) throws ArrayIndexOutOfBoundsException {
        boolean flg = false;
        Area nextArea = stage[playerY + Y][playerX + X];

        /*プレイヤーのいた場所を更新*/
        if (nextArea == Area.AIR || nextArea == Area.CLEAR_POINT) {//向かう先が空白か目的地なら動かす
            if (stage[playerY][playerX] == Area.MIX_MARK) {//プレイヤーがいた場所が
                stage[playerY][playerX] = Area.CLEAR_POINT; //目的地なら目印を設置
            } else {
                stage[playerY][playerX] = Area.AIR;//それ以外なら空白を設置
            }

            /*プレイヤーの移動先を更新*/
            if (nextArea == Area.CLEAR_POINT) {//プレイヤーが向かう場所が
                stage[playerY + Y][playerX + X] = Area.MIX_MARK; //目的地なら複合マークを設置
            } else {
                stage[playerY + Y][playerX + X] = Area.PLAYER;//それ以外ならプレイヤーを設置
            }

            /*プレイヤーの位置を更新*/
            playerX += X;
            playerY += Y;
            flg = true;
        }
        return flg;
    }

    private boolean moveBox(final int X, final int Y) throws ArrayIndexOutOfBoundsException {
        boolean flg = false;
        Area nextArea = stage[playerY + Y][playerX + X];
        if (nextArea == Area.BOX || nextArea == Area.CLEAR_MARK) {//一つ先が箱かクリアマークなら動かす
            int x = X * 2;
            int y = Y * 2;
            nextArea = stage[playerY + y][playerX + x];
            while (nextArea == Area.BOX || nextArea == Area.CLEAR_MARK) { //箱が連続していたら箱がなくなるまで進む
                x += X;
                y += Y;
                nextArea = stage[playerY + y][playerX + x];
            }
            if (nextArea != Area.WALL) { //壁がないなら動かす
                while (x != X || y != Y) {
                    if (stage[playerY + y][playerX + x] == Area.CLEAR_POINT) {//箱の移動先が
                        stage[playerY + y][playerX + x] = Area.CLEAR_MARK;//目印なら移動先にクリアマークを設置
                        clearCnt--;
                    } else {
                        stage[playerY + y][playerX + x] = Area.BOX;//それ以外なら箱を設置
                    }
                    x -= X;
                    y -= Y;
                    if (stage[playerY + y][playerX + x] == Area.CLEAR_MARK) {//元箱の位置が
                        stage[playerY + y][playerX + x] = Area.CLEAR_POINT;//クリアマークなら目的地を設置
                        clearCnt++;
                    } else {
                        stage[playerY + y][playerX + x] = Area.AIR;//それ以外は空白を設置
                    }
                }
                flg = true;
            }
        }
        return flg;
    }

}
