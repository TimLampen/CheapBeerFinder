import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Timothy Lampen on 11/23/2019.
 */
public class BeerChecker {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        ChromeDriver driver = new ChromeDriver(options);


        driver.get("https://beerxpress.ca/ProductDetails/search/keywords--");
        driver.executeScript("btnYes();");


        LinkedHashMap<String, String> beerLinks = new LinkedHashMap<>();
        Set<BeerBrand> beerBrands = new HashSet<>();
        int beers = driver.findElementsByClassName("brand-link").size();
        DecimalFormat df = new DecimalFormat("#.##");
        int loadedBeers = 0;
        beerLinks.put("Molson Canadian", "https://www.beerxpress.ca/Beers/Molson%20Canadian/651");

        for(WebElement beerBrand : driver.findElementsByClassName("brand-link")) {
            beerLinks.put(beerBrand.getText(), beerBrand.getAttribute("href"));
            if(loadedBeers > 3)
                break;
            else if(loadedBeers++%100==0)
                System.out.println(df.format(((double)loadedBeers)/beers*100) + "% done compiling beer names");
        }

        df = new DecimalFormat("#.###");
        int i = 0;

        beerLinks.put("Budweiser Prohibition Brew Non-Alcoholic 0.0", "https://www.beerxpress.ca/Beers/Budweiser%20Prohibition%20Brew%20Non-alcoholic%200.0/1376");


        for(Map.Entry<String,String> entry : beerLinks.entrySet()) {
            String name = entry.getKey();
            System.out.println(i++ + ": " + name);
            driver.get(entry.getValue());
            //driver.get("https://beerxpress.ca/Be  ers/Molson%20Canadian/651");
            //now we on the speciic beer brand page

            if (driver.findElementsByXPath("/html/body/form/div[9]/div[2]/div[1]/div/div/div[2]/span[3]").size() == 0)
                continue;

            double abv = Double.parseDouble(driver.findElementByXPath("/html/body/form/div[9]/div[2]/div[1]/div/div/div[2]/span[3]").getText().split(": ")[1].replace("%", "").split("\n")[0]) / 100.0;
            if(abv < 0.01)
                continue;

            Beer bestBeerCan = findBestBeer(driver, BeerType.CAN);
            Beer bestBeerBottle = findBestBeer(driver, BeerType.BOTTLE);


            BeerBrand brand = new BeerBrand(name, abv, (bestBeerBottle.getRatio() < bestBeerCan.getRatio() ? bestBeerBottle : bestBeerCan));
            System.out.println("Best beer for this brand = " + brand.getCheapestBeer().getType() + " " + brand.getCheapestBeer().getPackSize() + " x " + brand.getCheapestBeer().getBeerSize() + "mL ratio: " + df.format(brand.getCheapestBeer().getRatio()) + "\n");
            beerBrands.add(brand);
        }


        List<BeerBrand> sorted = beerBrands.stream().sorted(Comparator.comparing((brand) -> {
            if(brand.getABV()<1)
                return Double.MAX_VALUE;
            double totalAlcVolume = brand.getCheapestBeer().getBeerSize()  * brand.getCheapestBeer().getPackSize() * brand.getABV();
            System.out.println(brand.getName() + ": \n + " + totalAlcVolume + " / " + brand.getABV() + "% ratio: " + brand.getCheapestBeer().getCost()/totalAlcVolume);
            return brand.getCheapestBeer().getCost()/totalAlcVolume;
        })).collect(Collectors.toList());
        Collections.reverse(sorted);

        System.out.println("\n\n\n\n------------------------------------------------------");
        int counter = 1;
        for(BeerBrand brand : sorted){
            System.out.println(counter++ + ": " + brand.getName() + " - " + brand.getCheapestBeer().getType() + " " + brand.getCheapestBeer().getPackSize() + " x"  + brand.getCheapestBeer().getBeerSize() + "mL at $" + brand.getCheapestBeer().getCost());
        }

        driver.close();

    }




    private static Beer findBestBeer(ChromeDriver driver, BeerType beerType){
        int tableNum = beerType == BeerType.CAN ? 1 : 2;
        Beer beer = null;
        int tableRow = 1;
        double bestRatio = Integer.MAX_VALUE;//ratio is $/vol_pure_alcohol
        while(true){
            String packXPath = "/html/body/form/div[9]/div[2]/div[2]/div[1]/div[1]/table[" + tableNum + "]/tbody[" + tableRow++ + "]/tr/td[1]";
            if(driver.findElementsByXPath(packXPath).size()==0)
                break;
            String packDescription = driver.findElementByXPath(packXPath).getText();
            System.out.println(packXPath.replace("td[1]", "td[3]") + " / " + driver.findElementByXPath(packXPath.replace("td[1]", "td[3]")).getText());
            double price = Double.parseDouble(driver.findElementByXPath(packXPath.replace("td[1]", "td[3]")).getText().replace("$", ""));
            String[] descriptionSplit = packDescription.split(" ");
            int packSize = Integer.parseInt(descriptionSplit[0]);
            int beerSize = Integer.parseInt(descriptionSplit[3]);


            int totalVolume = packSize * beerSize;
            double ratio  = price / totalVolume;
            System.out.println("Comparing beer with " + totalVolume + "mL and price " + price);
            if(ratio < bestRatio) {
                bestRatio = ratio;
                beer = new Beer(beerType, packSize, beerSize, ratio, price);
            }

        }

        return beer == null ? new Beer(beerType, 1,1,Integer.MAX_VALUE, Integer.MAX_VALUE) : beer;
    }


}
