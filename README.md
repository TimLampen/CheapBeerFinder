# CheapBeerFinder
Scans the LCBO (Beer store in Ontario, Canada) to determine which beer is the cheapest (in terms of $/alcohol_ml - takes into consideration mL, % alc, and beer pack size).


## Motivation
As a student with an engineering background, I try to enjoy a nice weekend beer for as cheap as possible. However, there was no solution that currently exists which determines
which beer is the cheapest, especially when considering the various different alcohol percentages (from 4% - 10%) for light/dark beers. Therefore, I set out to create
a web automation script that scrapes the website for the most up-to-date prices and then outputs which beer should be purchased if you're on a strict budget.

## Installation
1. Install maven dependencies `mvn clean install`
2. Run the method in `BeerChecker`
3. The process will take a while (there are a lot of beers) but it is worth it!
