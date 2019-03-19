package server;

import common.Helper;
import common.StickType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private AtomicInteger clientCount;
    private volatile int rounds;
    private final int tableSize;
    private Player[] players;
    private volatile int playerIndex;
    private StickType[] sticks;

    private CyclicBarrier fullTableBarrier;
    private CountDownLatch bidsLatch;
    private CountDownLatch gameLatch;

    public Game(int rounds, int tableSize) {
        this.rounds = rounds;
        this.tableSize = tableSize;
        this.clientCount = new AtomicInteger();
        this.players = new Player[tableSize];
        this.playerIndex = 0;
        this.fullTableBarrier = new CyclicBarrier(tableSize);
        this.bidsLatch = new CountDownLatch(tableSize - 1);
        this.gameLatch = new CountDownLatch(rounds);
        shuffleSticks();
    }

    private void shuffleSticks() {
        sticks = new StickType[tableSize];
        for (int i = 0; i < tableSize; i++) {
            sticks[i] = StickType.NORMAL;
        }
        int shortIndex = Helper.randomInt(0, tableSize - 1);
        sticks[shortIndex] = StickType.SHORT;
        for (int i = 0; i < tableSize; i++) {
            System.out.print(sticks[i] + " ");
        }
        System.out.println();
    }

    public synchronized StickType draw(Player player, int stickIndex) {
        StickType drawnStick = null;
        int stickCounter = 0;
        for (int i = 0; i < tableSize; i++) {
            if (sticks[i] != null) {
                stickCounter++;
            }
            if (stickCounter == stickIndex) {
                drawnStick = sticks[i];
                sticks[i] = null;
                break;
            }
        }

        for (int i = 0; i < tableSize; i++) {
            System.out.print(sticks[i] + " ");
        }
        System.out.println();

        for (Player p : players) {
            if (p != player && p.getBid().equals(drawnStick)) {
                p.inceremntPoints();
            }
        }

        bidsLatch = new CountDownLatch(tableSize - 1);
        rounds--;
        playerIndex = (playerIndex + 1) % tableSize;
        gameLatch.countDown();

        return drawnStick;
    }

    public synchronized void bid(Player player, StickType stickType) {
        for (Player p : players) {
            if (p == player) {
                p.setBid(stickType);
            }
        }
        bidsLatch.countDown();
    }

    public synchronized void eject(Player player) {
        for (int i = 0; i < tableSize; i++) {
            if (players[i] == player) {
                players[i] = null;
            }
        }
        shuffleSticks();
        playerIndex = 0;
    }

    public synchronized Player currentPlayer() {
        return players[playerIndex];
    }

    public synchronized Boolean giveSeat(Player player) {
        for (int i = 0; i < tableSize; i++) {
            if (players[i] == null) {
                players[i] = player;
                return true;
            }
        }
        return false;
    }

    public synchronized int stickCount() {
        int counter = 0;
        for (StickType s : sticks) {
            if (s != null) counter++;
        }
        return counter;
    }

    public void waitForBids() throws InterruptedException {
        bidsLatch.await();
    }

    public void waitForFullTable() throws BrokenBarrierException, InterruptedException {
        fullTableBarrier.await();
    }

    public synchronized int getRounds() {
        return rounds;
    }

    public CountDownLatch getGameLatch() {
        return gameLatch;
    }

    public synchronized void printResult() {
        System.out.println("----------------------------------");
        Arrays.sort(players, (a, b) -> {
            int aPoints = (a == null ? -1 : a.getPoints());
            int bPoints = (b == null ? -1 : b.getPoints());
            return Integer.compare(bPoints, aPoints);
        });
        for (Player p : players) {
            if (p != null) {
                System.out.println(p.getId() + " - " + p.getPoints());
            }
        }
        System.out.println("----------------------------------");
    }
}
