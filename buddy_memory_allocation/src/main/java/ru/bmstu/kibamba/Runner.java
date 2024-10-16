package ru.bmstu.kibamba;

public class Runner {
    public static void main(String[] args){
        int mainSize = 128;

        Buddy buddy = new Buddy(mainSize);
        buddy.allocate(16);
        buddy.allocate(16);
        buddy.allocate(16);
        buddy.allocate(64);
        buddy.allocate(32);
        buddy.deallocate(0);
        buddy.deallocate(32);
        buddy.allocate(32);
    }
}
