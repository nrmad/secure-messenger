package networking;

public class TBoard {

    private TBoard tBoard = new TBoard();
    private boolean cnuRecieved = false;

    private TBoard(){

    }

    public TBoard getInstance(){
        return tBoard;
    }

    public boolean getCnuRecieved(){
        return cnuRecieved;
    }

    public void setCnuRecieved(boolean cnuRecieved){
        this.cnuRecieved = cnuRecieved;
    }



}
