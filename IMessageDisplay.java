interface IMessageDisplay {
    public static final int NORMAL = 0;
    public static final int VERBOSE = 1;
    void showMessage(String msg, int level);
    void showMessage(String msg);
}
