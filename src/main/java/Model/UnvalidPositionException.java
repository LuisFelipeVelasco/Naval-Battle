package Model;



public class UnvalidPositionException extends Exception {


    public UnvalidPositionException(int row, int column) {
        super("Unvalid Position on row " + row + ", column " + column);
    }
}
