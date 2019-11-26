/**
 * Created by Timothy Lampen on 11/23/2019.
 */
public class Beer {

    private int packSize = 0;
    private double cost = 0;
    private int beerSize = 0;
    private double ratio = 0;
    BeerType type;

    public Beer(BeerType type, int packSize, int beerSize, double ratio, double cost){
        this.packSize = packSize;
        this.beerSize = beerSize;
        this.cost = cost;
        this.ratio = ratio;
        this.type = type;
    }

    public BeerType getType() {
        return type;
    }

    public double getRatio() {
        return ratio;
    }

    public double getCost() {
        return cost;
    }

    public int getBeerSize() {
        return beerSize;
    }

    public int getPackSize() {
        return packSize;
    }
}
