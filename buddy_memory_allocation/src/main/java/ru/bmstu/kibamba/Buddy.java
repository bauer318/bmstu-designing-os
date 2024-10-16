package ru.bmstu.kibamba;

import java.util.ArrayList;
import java.util.HashMap;

public class Buddy {
    // Inner class to store lower and upper bounds of the allocated memory
    static class Pair {
        int lowerBound;
        int upperBound;

        Pair(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

    }

    int mainMemorySize;
    // Array to track all the free nodes of various sizes
    ArrayList<Pair>[] pairArrayList;

    // Hashmap to store the starting
    // address and size of allocated segment
    // Key is starting address, size is value
    HashMap<Integer, Integer> blockStartingAddressSize;

    // Else compiler will give warning about generic array creation
    @SuppressWarnings("unchecked")
    public Buddy(int s) {
        mainMemorySize = s;
        blockStartingAddressSize = new HashMap<>();

        // Gives us all possible powers of 2
        int x = (int) Math.ceil(Math.log(s) / Math.log(2));

        // One extra element is added to simplify arithmetic calculations
        pairArrayList = new ArrayList[x + 1];

        for (int i = 0; i <= x; i++) {
            pairArrayList[i] = new ArrayList<>();
        }

        // Initially, only the largest block is free and hence is on the free list
        pairArrayList[x].add(new Pair(0, mainMemorySize - 1));
    }

    public void allocate(int s) {
        // Calculate which free list to search to get the smallest block large enough to fit the request
        int x = (int) Math.ceil(Math.log(s) / Math.log(2));

        int i;
        Pair temp;

        // We already have such a block
        if (pairArrayList[x].size() > 0) {
            // Remove from free list as it will be allocated now
            temp = pairArrayList[x].remove(0);
            System.out.println("Memory from " + temp.lowerBound + " to " + temp.upperBound + " allocated");
            //Store in HashMap
            blockStartingAddressSize.put(temp.lowerBound, temp.upperBound - temp.lowerBound + 1);
            return;
        }

        // If not, search for a larger block
        for (i = x + 1; i < pairArrayList.length; i++) {
            if (pairArrayList[i].size() == 0) {
                continue;
            }
            // Found a larger block, so break
            break;
        }

        // This would be true if no such block was found and array was exhausted
        if (i == pairArrayList.length) {
            System.out.println("Sorry, failed to allocate memory");
            return;
        }

        // Remove the first block
        temp = pairArrayList[i].remove(0);

        i--;

        // Traverse down the list
        for (; i >= x; i--) {
            // Divide the block in two halves
            // lower index to half-1
            Pair newPair = new Pair(temp.lowerBound, temp.lowerBound
                    + (temp.upperBound - temp.lowerBound) / 2);

            // half to upper index
            Pair newPair2 = new Pair(temp.lowerBound
                    + (temp.upperBound - temp.lowerBound + 1) / 2, temp.upperBound);

            // Add the second half-pair to next list which is tracking blocks of smaller size
            pairArrayList[i].add(newPair2);

            temp = newPair;
        }

        System.out.println("Memory from " + temp.lowerBound
                + " to " + temp.upperBound + " allocated");

        //Store the allocated block address and it size
        blockStartingAddressSize.put(temp.lowerBound, temp.upperBound - temp.lowerBound + 1);
    }

    public void deallocate(int s) {
        if (!blockStartingAddressSize.containsKey(s)) {
            System.out.println("Invalid free request " + s);
            return;
        }

        int x = (int) Math.ceil(Math.log(blockStartingAddressSize.get(s)) / Math.log(2));
        int i;
        int buddyNumber;
        int buddyAddress;

        pairArrayList[x].add(new Pair(s, s + (int) Math.pow(2, x) - 1));
        System.out.println("Memory block from " + s + " to " + (s + (int) Math.pow(2, x) - 1) + " freed");

        buddyNumber = s / blockStartingAddressSize.get(s);

        buddyAddress = buddyNumber % 2 != 0 ? s - (int) Math.pow(2, x) : s + (int) Math.pow(2, x);

        //Search in the free list for buddy
        for (i = 0; i < pairArrayList[x].size(); i++) {
            //This indicates the buddy is also free
            if (pairArrayList[x].get(i).lowerBound == buddyAddress) {
                if (buddyNumber % 2 == 0) {
                    //Buddy is the block after block with this base address
                    //Add to appropriate free list
                    pairArrayList[x + 1].add(new Pair(s, s + 2 * ((int) Math.pow(2, x)) - 1));
                    System.out.println("Coalescing of blocks starting at " + s + " and " + buddyAddress + " was done");
                } else {
                    //Buddy is the block before block with this base address. Add to appropriate free list
                    pairArrayList[x + 1].add(new Pair(buddyAddress, buddyAddress + 2 * ((int) Math.pow(2, x)) - 1));
                    System.out.println("Coalescing of blocks starting at " + buddyAddress + " and " + s + " was done");
                }
                // Remove the individual segments as they have coalesced
                pairArrayList[x].remove(i);
                pairArrayList[x].remove(pairArrayList[x].size() - 1);
                break;
            }
        }
        // Remove entry from HashMap
        blockStartingAddressSize.remove(s);
    }

}
