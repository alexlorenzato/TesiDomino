public class Tile {
    int val_1, val_2;
    int owner; // 1:P1, 2:P2

    public Tile(){}


    public Tile(int val_1, int val_2) {
        this.val_1 = val_1;
        this.val_2 = val_2;
        this.owner = -1;
    }
    
    public Tile(int val_1, int val_2, int owner) {
        this.val_1 = val_1;
        this.val_2 = val_2;
        this.owner = owner;
    }

    public static void printTile(Tile t) {
        System.out.print(t.val_1 + "|" + t.val_2 + "(" + t.owner + ")");
    }
}