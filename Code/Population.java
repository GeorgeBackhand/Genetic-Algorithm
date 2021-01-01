import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Population {

    FitnessCalculator fit = new FitnessCalculator();

    char[][] shreddedDocument = fit.getShreddedDocument("src/document3-shredded.txt"); //This reads a -shredded.txt file from disk\
    char[][] unShreddedDocument;
    int[] check = new int[shreddedDocument.length];

    double total;
    //create our seed
    long seed = 566;
    Random r = new Random(seed);

    //Parameters
    int popSize = 300; //population size
    int MAXGEN = 100; //max generations

    int numberofElites = 15; //number of elites you want to make
    int tournamentSize = 25; //the tournament size
    int parents = 3; //the number of parents from the tournament you will make

    int crossoverRate = 90; //crossOver Rate
    int mutationRate = 0; //mutation Rate

    int pickCrossOver = 0; // 0 = orderCrossOver. 1 = UniformCrossOver

    public Population() {

        ArrayList<Individual> pop = new ArrayList<>(); //array list
        ArrayList<Individual> genePool = new ArrayList<>(); //Gene Pool

        //create out population
        for (int k = 0; k < popSize; k++) { //create population size
            int[] Chromosome = new int[shreddedDocument.length]; //permutation of size document length
            for (int i = 0; i < Chromosome.length; i++) {
                Chromosome[i] = r.nextInt(Chromosome.length); //random number from 0 to the chromosomes length
                check[i] = Chromosome[i];
                if (i == 0) {
                    continue;
                }
                for (int j = 0; j < i; j++) {
                    if (Chromosome[i] == check[j]) {
                        i--;
                        continue;
                    }
                }
            }
            Individual single = new Individual(Chromosome, 0);
            pop.add(single); //insert each chromosome into the Array List
        }
        //print(); // print out the data we set all the variables to
        geneticAlgorithm(pop, genePool); //call my genetic Algorithm method passing in my population

        //print out the document(solution)
        //unShreddedDocument = fit.unshred(shreddedDocument, genePool.get(0).perm);
        //fit.prettyPrint(unShreddedDocument);
    }

    public void geneticAlgorithm(ArrayList<Individual> pop, ArrayList<Individual> genePool) {

        int finalLine = 0;
        for (int i = 1; i < MAXGEN; i++) {
            for (int j = 0; j < pop.size(); j++) {
                pop.get(j).fitness = fit.fitness(shreddedDocument, pop.get(j).perm); //create fitness for each individual in pop
            }
            getElites(pop, shreddedDocument, genePool, numberofElites); //get my Elites. numberofElites = how many elites you want to create
            tournament(pop, genePool, shreddedDocument, fit, parents); //get my Parents. parents = how many parents you want to create
            while (pop.size() <= popSize) {
                if (pickCrossOver == 0) {
                    orderCrossOver(genePool, shreddedDocument, crossoverRate, pop); //create 2 children from my crossOver
                }
                if (pickCrossOver == 1) {
                    UniFormCrossOver(genePool, shreddedDocument, crossoverRate, pop);
                }
            }
            sortFitness(genePool);
            average(genePool);
            //System.out.print(" GENERATION " + i + " " + "        Average Fitness: " + total + "          Best Fitness: " + genePool.get(0).fitness + "                      Best Chromosome: ");
            System.out.println(genePool.get(0).fitness);
            //print out our best fitness chromosome
           /* for (int k = 0; k < genePool.get(0).perm.length; k++) {
                System.out.print(genePool.get(0).perm[k] + " ");
            }*/
            /*finalLine++;
            if (finalLine == MAXGEN - 1) {
                System.out.println();
                System.out.println("BEST SOLUTION FITNESS: " + genePool.get(0).fitness);
                System.out.print("BEST SOLUTION CHROMOSOME: ");
                for (int k = 0; k < genePool.get(0).perm.length; k++) {
                    System.out.print(genePool.get(0).perm[k] + " ");
                }
            }
            System.out.println();*/
        }
    }

    public void average(ArrayList<Individual> genePool){ //get the average for each generations fitness

        total = 0;
        for(int i = 0; i < genePool.size(); i++){
            total += genePool.get(i).fitness;
        }
        total = total / genePool.size();
    }

    public void sortFitness(ArrayList<Individual> pop){ //sort the fitness from best to worst

        int temp = 0;
        for(int i = 1; i < pop.size(); i++){ //start from index 1 to the populations size
            temp = i; //set the index to temp
            while( temp > 0 && pop.get(temp - 1).fitness > pop.get(temp).fitness){
                swap(pop, temp, temp - 1);
                temp = temp - 1;
            }
        }
    }

    public void swap(ArrayList<Individual> pop, int left, int right){
        Individual temp = pop.get(left);
        pop.set(left,pop.get(right));
        pop.set(right, temp);
    }

    public void mutate(Individual child, int mutationRate, ArrayList<Individual> pop) {

        int rng = r.nextInt(100); //get a random # between 0 and 100
        if (rng < mutationRate) { //if the random value is < the given mutation
            int randA = r.nextInt((child.perm.length));
            int randB = r.nextInt((child.perm.length));

            while (randA == randB) { //if random numbers are the same get new random numbers
                randA = r.nextInt((child.perm.length));
                randB = r.nextInt((child.perm.length));
            }

            //Reciprocal Exchange (Mutation)
            int temp = child.perm[randA];
            child.perm[randA] = child.perm[randB];
            child.perm[randB] = temp;
        }
        pop.add(child);
    }

    public void getElites(ArrayList<Individual> population, char[][] shreddedDocument, ArrayList<Individual> genePool, int numberofElites) {
            //Get my Elites
            int index = 0;
            for (int r = 0; r < numberofElites; r++) {
                double bestFit = Integer.MAX_VALUE;
                for (int j = 0; j < population.size(); j++) {
                    double fit = population.get(j).fitness; //gets the fitness for that index
                    if (bestFit > fit) {  //we want the smallest fitness
                        bestFit = fit; //new best fit
                        index = j;
                    }
                }
                Individual elite = new Individual(population.get(index).perm, fit.fitness(shreddedDocument, population.get(index).perm));
                genePool.add(elite); //add elite into my genePool
                population.remove(index); //remove the elite from the original population array list
            }
        }

    //get our best value for each chromosome
    public void tournament(ArrayList<Individual> pop, ArrayList<Individual> genePool, char[][] shreddedDocument, FitnessCalculator fit, int parents) {
        Individual parent = null;
        int index = 0;

        for (int j = 0; j < parents; j++) {
            double bestFit = Integer.MAX_VALUE;
            for (int i = 0; i < tournamentSize; i++) {
                int randNum = r.nextInt(pop.size()); //0 -> population size
                double tempFit = pop.get(randNum).fitness; //gets the fitness for that population
                if (bestFit > tempFit) {  //we want the smallest fitness
                    bestFit = tempFit; //new best fit
                    index = randNum;
                }
            }
            parent = new Individual(pop.get(index).perm, pop.get(index).fitness);
            genePool.add(parent);
            pop.add(parent);
        }

        pop.clear(); //clear my population linked list

        if(pop.size() == 0){
            for (int k = 0; k < numberofElites; k++) {
                pop.add(genePool.get(k));
            }
        }

    }

    public void orderCrossOver(ArrayList<Individual> genePool, char[][] shreddedDocument, int crossoverRate, ArrayList<Individual> pop) {

        int rng = r.nextInt(100); //get a random # between 0 and 100
        if (rng < crossoverRate) {
            //Pick 2 people at random from our genePool
            // run twice to create 2 different children
            int firstParent = r.nextInt(genePool.size());
            int secondParent = r.nextInt(genePool.size());
            while (firstParent == secondParent) {
                firstParent = r.nextInt(genePool.size());
                secondParent = r.nextInt(genePool.size());
            }
            //now we have our 2 parents we will be using for crossOver
            Individual parent1 = new Individual(genePool.get(firstParent).perm, genePool.get(firstParent).fitness);
            Individual parent2 = new Individual(genePool.get(secondParent).perm, genePool.get(secondParent).fitness);

            //randomly selected set from the first parent
            //get 2 random values from first parent to use from 0 -> perm length
            //initialize the child

            int first = r.nextInt(parent1.perm.length);
            int second = r.nextInt(parent1.perm.length);

            int[] childA = new int[parent1.perm.length]; //create our array size for the child
            int[] childB = new int[parent2.perm.length];
            for (int i = 0; i < parent1.perm.length; i++) { //set initial values for the child to -1
                childA[i] = -1;
                childB[i] = -1;
            }

            while (!(first < second)) { //if first is not less than second in the parent1 perm array get new random values
                first = r.nextInt(parent1.perm.length);
                second = r.nextInt(parent1.perm.length);
            }

            for (int i = first; i <= second; i++) { //put the elements from first into the child at that index
                childA[i] = parent1.perm[i];
                childB[i] = parent2.perm[i];
            }

            int index = 0;
            int index2 = 0;
            for (int j = 0; j < parent2.perm.length; j++) {
                boolean check = true;
                boolean check2 = true;

                //ChildA
                for (int k = 0; k < childA.length; k++) {
                    if (parent2.perm[j] == childA[k]) { //if the value exists already in the child then dont add it
                        check = false;
                        break;
                    }
                }

                //ChildB
                for (int k = 0; k < childB.length; k++) {
                    if (parent1.perm[j] == childB[k]) { //if the value exists already in the child then dont add it
                        check2 = false;
                        break;
                    }
                }

                //ChildA
                if (childA[index] == -1 && check == true) {
                    childA[index] = parent2.perm[j];
                    index++;
                } else {
                    if (check == true) {
                        index = j;
                            while (childA[index] != -1) {
                                index++;
                        }
                            if(index != 15) {
                                childA[index] = parent2.perm[j];
                            }
                    }
                }

                //ChildB
                if (childB[index2] == -1 && check2 == true) {
                    childB[index2] = parent1.perm[j];
                    index2++;
                } else {
                    if (check2 == true) {
                        index2 = j;
                            while (childB[index2] != -1) {
                                index2++;

                        }
                            childB[index2] = parent1.perm[j];
                    }
                }
            }

            Individual child1 = new Individual(childA, fit.fitness(shreddedDocument, childA));
            Individual child2 = new Individual(childB, fit.fitness(shreddedDocument, childB));
            mutate(child1, mutationRate, pop);
            mutate(child2, mutationRate, pop);
        }
    }

    public void UniFormCrossOver(ArrayList<Individual> genePool, char[][] shreddedDocument, int crossoverRate, ArrayList<Individual> pop){

        int rng = r.nextInt(100); //get a random # between 0 and 100
        if (rng < crossoverRate) {

            //Pick 2 people at random from our genePool
            // run twice to create 2 different children
            int firstParent = r.nextInt(genePool.size());
            int secondParent = r.nextInt(genePool.size());
            while (firstParent == secondParent) {
                firstParent = r.nextInt(genePool.size());
                secondParent = r.nextInt(genePool.size());
            }
            //now we have our 2 parents we will be using for UniFormCrossOver
            Individual parent1 = new Individual(genePool.get(firstParent).perm, genePool.get(firstParent).fitness);
            Individual parent2 = new Individual(genePool.get(secondParent).perm, genePool.get(secondParent).fitness);

            int[] childA = new int[parent1.perm.length]; //create our array size for the child
            int[] childB = new int[parent2.perm.length];
            int[] mask = new int[parent1.perm.length]; //create our mask size
            Random rand = new Random();

            for (int i = 0; i < parent1.perm.length; i++) { //set initial values for the child to -1
                childA[i] = -1;
                childB[i] = -1;
            }

            for (int j = 0; j < mask.length; j++) { //go through the masks length
                mask[j] = rand.nextInt(2); //generate at each index a random number 0 or 1
            }

            for (int i = 0; i < mask.length; i++) { //check if masks index is a 1 from parent copy value into the child at that index
                if (mask[i] == 1) {
                    childA[i] = parent1.perm[i];
                    childB[i] = parent2.perm[i];
                }
            }

            int index = 0;
            int index2 = 0;
            for (int j = 0; j < parent2.perm.length; j++) {
                boolean check = true;
                boolean check2 = true;

                //ChildA
                for (int k = 0; k < childA.length; k++) {
                    if (parent2.perm[j] == childA[k]) { //if the value exists already in the child then dont add it
                        check = false;
                        break;
                    }
                }

                //ChildB
                for (int k = 0; k < childB.length; k++) {
                    if (parent1.perm[j] == childB[k]) { //if the value exists already in the child then dont add it
                        check2 = false;
                        break;
                    }
                }

                //ChildA
                if (check == true) {
                    index = 0;
                    while (childA[index] != -1) {
                        index++;
                    }
                    childA[index] = parent2.perm[j];
                }

                //ChildB
                if (check2 == true) {
                    index2 = 0;
                    while (childB[index2] != -1) {
                        index2++;
                    }
                    childB[index2] = parent1.perm[j];
                }
            }
            Individual child1 = new Individual(childA, fit.fitness(shreddedDocument, childA));
            Individual child2 = new Individual(childB, fit.fitness(shreddedDocument, childB));

            mutate(child1, mutationRate, pop);
            mutate(child2, mutationRate, pop);
        }
    }

    public void print(){ //PRINT ALL OF MY PARAMETERS
        System.out.println("Seed: " + seed);
        System.out.println("Population Size: " + popSize);
        System.out.println("Generations Size: " + MAXGEN);
        System.out.println("# of Elites: " + numberofElites);
        System.out.println("Tournament Size: " + tournamentSize);
        System.out.println("# of Parents from Tournament: " + parents);
        System.out.println("CrossOver Rate: " + crossoverRate);
        System.out.println("Mutation Rate: " + mutationRate);
    }

    public static void main(String[] args) {
        Population p = new Population();
    }
}
