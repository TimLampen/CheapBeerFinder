/**
 * Created by Timothy Lampen on 11/23/2019.
 */
public class BeerBrand {
    private double ABV = 0;
    private String name = "";
    private Beer beer;

    public BeerBrand(String name, double ABV, Beer beer){
        this.name = name;
        this.ABV = ABV;
        this.beer = beer;
    }

    public double getABV() {
        return ABV;
    }

    public String getName() {
        return name;
    }

    public Beer getCheapestBeer() {
        return beer;
    }
}
