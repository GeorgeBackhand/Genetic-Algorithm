# Genetic-Algorithm-GA-
Implementation of a genetic algorithm system. Testing for 'best fitness' using various Crossover rates, mutation rates, and number of  elites.


    //create our seed
    long seed = 567;
    Random r = new Random(seed);

    //Parameters
    int popSize = 200; //population size
    int MAXGEN = 150; //max generations

    int numberofElites = 25; //number of elites you want to make
    int tournamentSize = 5; //the tournament size
    int parents = 3; //the number of parents from the tournament you will make

    int crossoverRate = 100; //crossOver Rate
    int mutationRate = 0; //mutation Rate

    int pickCrossOver = 0; // 0 = orderCrossOver. 1 = UniformCrossOver

-> change the parameters to whatever values you want

-> change popSize = 300, MAXGEN = 200 if you wanna run the larger documents to get there best fitnesses. document2, document3.

All documents will fully decode to the correct text using the parameters I gave.
