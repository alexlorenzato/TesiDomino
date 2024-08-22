public class Tile {
    int val_1, val_2;
    int owner; // 0:P1, 1:P2
    int index; 

    
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
    
    public Tile(int val_1, int val_2, int owner, int index) {
        this.val_1 = val_1;
        this.val_2 = val_2;
        this.owner = owner;
        this.index = index; 
    }

    public static void printTile(Tile t) {
        System.out.print(t.val_1 + "|" + t.val_2  + "[" + t.index + "]" + "(" + t.owner + ")"); 
    }

    public void printTile() {
        System.out.print(val_1 + "|" + val_2  + "[" + index + "]" + "(" + owner + ")"); 
    }

    public void swapTile(){
        int tmp = this.val_1;
        this.val_1 = this.val_2;
        this.val_2 = tmp;
    }
}