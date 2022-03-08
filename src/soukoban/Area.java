package soukoban;

public enum Area {
    AIR("　", 0), WALL("■", 1), BOX("□", 2), CLEAR_POINT("×", 3), PLAYER("〇", 4), MIX_MARK("⊗", 5), CLEAR_MARK("☑", 6);

    private final String label;
    private final int id;

    Area(String label, int id) {
        this.label = label;
        this.id = id;
    }

    /**
     * 自身の文字列表現を戻します
     *
     * @return 自身の文字列表現
     */
    public String getLabel() {
        return label;
    }

    /**
     * 自身の文字列表現を戻します
     *
     * @return 自身の文字列表現
     */
    public String toString() {
        return label;
    }

    /**
     * 自身のIDを戻します
     * - AIR:0
     * - WALL:1
     * - BOX:2
     * - CLEAR_POINT:3
     * - PLAYER:4
     * - MIX_MARK:5
     * - CLEAR_MARK:6
     *
     * @return 自身のID
     */
    public int getId() {
        return id;
    }

    /**
     * 渡された整数と同じIDを持つAreaオブジェクトを戻します。
     * - 0:AIR
     * - 1:WALL
     * - 2:BOX
     * - 3:CLEAR_POINT
     * - 4:PLAYER
     * - 5:MIX_MARK
     * - 6:CLEAR_MARK
     *
     * @param id 渡す整数
     * @return 渡された整数と同じIDを持つAreaオブジェクト
     * @throws IllegalArgumentException 渡された整数に対するAreaオブジェクトがないとき
     */
    public static Area getById(int id) {

        for (Area area : Area.values()) { //拡張for文による走査
            if (area.getId() == id) {
                return area; //条件に一致するインスタンスを返す
            }
        }
        throw new IllegalArgumentException("指定したIDはAreaに存在しません: " + id);
    }
}
