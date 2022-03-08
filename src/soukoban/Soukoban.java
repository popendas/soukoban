package soukoban;

import java.util.*;
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

    /**
     * 渡された整数型二次配列をデフォルトステージとして読み込み、ゲームの準備をします。
     *
     * @param defaultStageToInt ステージとして渡す整数型二次配列
     */
    public Soukoban(int[][] defaultStageToInt) {
        setStage(defaultStageToInt);
    }

    /**
     * 渡されたArea型二次配列をデフォルトステージとして読み込み、ゲームの準備をします。
     *
     * @param defaultStage ステージとして渡すArea型二次配列
     */
    public Soukoban(Area[][] defaultStage) {
        setStage(defaultStage);
    }

    public Soukoban() {
    }

    /**
     * プレイヤーを下へ移動させます。
     */
    public void moveDown() {
        move(down);
    }

    /**
     * プレイヤーを左へ移動させます。
     */
    public void moveLeft() {
        move(left);

    }

    /**
     * プレイヤーを右へ移動させます。
     */
    public void moveRight() {
        move(right);
    }

    /**
     * プレイヤーを上へ移動させます。
     */
    public void moveUp() {
        move(up);
    }

    /**
     * ステージを一手前の状態に戻します。
     */
    public void back() {
        if (!LOG.isEmpty()) {
            LOG.remove(LOG.size() - 1);
            stageReset();
            LOG.forEach(BooleanSupplier::getAsBoolean);
        }
    }

    /**
     * ステージをデフォルトの状態に戻します。
     */
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

    /**
     * ステージの状態を表すArea型二次リストを戻します。
     *
     * @return Area型二次リスト(読み取り専用)
     */
    public List<List<Area>> getStage() {
        List<List<Area>> list = new ArrayList<>(this.stage.length + 2);
        for (Area[] line : this.stage) {
            list.add(Arrays.asList(line));
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * プレイヤーが動いた回数を戻します。
     *
     * @return プレイヤーが動いた回数
     */
    public int getMoveCount() {
        return LOG.size();
    }

    /**
     * 渡された整数型二次配列をデフォルトステージとして読み込み、ステージをリセットします。
     *
     * @param defaultStageToInt ステージとして渡す整数型二次配列
     */
    public void setStage(int[][] defaultStageToInt) {
        defaultStage = new Area[defaultStageToInt.length][];
        //int配列をArea配列に変換
        for (int i = 0; i < defaultStageToInt.length; i++) {
            Area[] line = new Area[defaultStageToInt[i].length];
            for (int j = 0; j < line.length; j++) {
                line[j] = Area.getById(defaultStageToInt[i][j]);
            }
            defaultStage[i] = line;
        }
        //this.defaultStageにセット
        setStage(defaultStage);
    }

    /**
     * 渡されたArea型二次配列をデフォルトステージとして読み込み、ステージをリセットします。
     *
     * @param defaultStage ステージとして渡すArea型二次配列
     */
    public void setStage(Area[][] defaultStage) {
        this.defaultStage = defaultStage;
        reset();
    }

    /**
     * ステージの状態の文字列表現を戻します。
     *
     * @return ステージの状態の文字列表現
     */
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
        //デフォルトステージをコピー
        stage = new Area[defaultStage.length][];
        for (int i = 0; i < defaultStage.length; i++) {
            stage[i] = defaultStage[i].clone();
        }
        //プレイヤーの場所と目的地の数をセット
        playerSearch();
        countClear();
    }

    private void countClear() {
        clearCnt = 0;
        //目的地の数をカウント
        for (Area[] areaArray : defaultStage) {
            for (Area area : areaArray) {
                if (area == Area.CLEAR_POINT || area == Area.MIX_MARK) {
                    clearCnt++;
                }
            }
        }
    }

    private void playerSearch() {
        //プレイヤーか複合マークを探索
        for (int i = 0; i < stage.length; i++) {
            for (int j = 0; j < stage[i].length; j++) {
                if (stage[i][j] == Area.PLAYER || stage[i][j] == Area.MIX_MARK) {
                    playerX = j;
                    playerY = i;
                    return;
                }
            }
        }
        //見つからなかったら例外
        throw new IllegalArgumentException("プレイヤーが設置されていません");
    }

    private void move(BooleanSupplier bSupp) {
        if (bSupp.getAsBoolean())
            //移動できたならLOGに記録
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

        //プレイヤーのいた場所を更新
        //向かう先が空白か目的地なら動かす
        if (nextArea == Area.AIR || nextArea == Area.CLEAR_POINT) {
            //プレイヤーがいた場所が
            if (stage[playerY][playerX] == Area.MIX_MARK) {
                //目的地なら目印を設置
                stage[playerY][playerX] = Area.CLEAR_POINT;
            } else {
                //それ以外なら空白を設置
                stage[playerY][playerX] = Area.AIR;
            }

            //プレイヤーの移動先を更新
            //プレイヤーが向かう場所が
            if (nextArea == Area.CLEAR_POINT) {
                //目的地なら複合マークを設置
                stage[playerY + Y][playerX + X] = Area.MIX_MARK;
            } else {
                //それ以外ならプレイヤーを設置
                stage[playerY + Y][playerX + X] = Area.PLAYER;
            }

            //プレイヤーの位置を更新
            playerX += X;
            playerY += Y;
            flg = true;
        }
        return flg;
    }

    private boolean moveBox(final int X, final int Y) throws ArrayIndexOutOfBoundsException {
        boolean flg = false;
        Area nextArea = stage[playerY + Y][playerX + X];
        if (nextArea == Area.BOX || nextArea == Area.CLEAR_MARK) {
            //一つ先が箱かクリアマークなら動かす
            int x = X * 2;
            int y = Y * 2;
            nextArea = stage[playerY + y][playerX + x];
            while (nextArea == Area.BOX || nextArea == Area.CLEAR_MARK) {
                //箱が連続していたら箱がなくなるまで進む
                x += X;
                y += Y;
                nextArea = stage[playerY + y][playerX + x];
            }
            if (nextArea != Area.WALL) {
                //壁がないなら動かす

                //一番遠くの箱を動かす
                //箱の移動先が
                if (stage[playerY + y][playerX + x] == Area.CLEAR_POINT) {
                    //目印なら移動先にクリアマークを設置
                    stage[playerY + y][playerX + x] = Area.CLEAR_MARK;
                    clearCnt--;
                } else {
                    //それ以外なら箱を設置
                    stage[playerY + y][playerX + x] = Area.BOX;
                }

                //プレイヤーの目の前の箱を動かす
                x = X;
                y = Y;
                //元箱の位置が
                if (stage[playerY + y][playerX + x] == Area.CLEAR_MARK) {
                    //クリアマークなら目的地を設置
                    stage[playerY + y][playerX + x] = Area.CLEAR_POINT;
                    clearCnt++;
                } else {
                    //それ以外は空白を設置
                    stage[playerY + y][playerX + x] = Area.AIR;
                }

                flg = true;
            }
        }
        return flg;
    }

}
