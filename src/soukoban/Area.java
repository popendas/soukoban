package soukoban;

public enum Area {
    AIR("　", 0), WALL("■", 1), BOX("□", 2), CLEAR_POINT("×", 3), PLAYER("〇", 4), MIX_MARK("⊗", 5), CLEAR_MARK("☑", 6);

    private final String label;
    private final int id;

    Area(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    public static Area getById(int id) {

        for (Area area : Area.values()) { //拡張for文による走査
            if (area.getId() == id) {
                return area; //条件に一致するインスタンスを返す
            }
        }
        throw new IllegalArgumentException("指定したIDはAreaに存在しません: " + id);
    }
}
