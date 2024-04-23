import java.util.Random;

class Bank {
    private double[] accounts;

    public Bank(int n, double initBalance) {
        accounts = new double[n];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = initBalance;
        }
    }

    public synchronized int size() {
        return accounts.length;
    }

    public synchronized double getTotalBalance() {
        double total = 0;
        for (double balance : accounts) {
            total += balance;
        }
        return total;
    }

    public synchronized void transfer(int from, int to, double amount) throws InterruptedException {
        while (accounts[from] < amount) {
            System.out.println(Thread.currentThread().getName() + " đang chờ...");
            wait();
        }

        accounts[from] -= amount;
        accounts[to] += amount;

        System.out.println("Chuyển " + amount + " từ tài khoản " + from + " sang tài khoản " + to + ". Số dư tổng cộng mới: " + getTotalBalance());

        notifyAll();
    }
}

class TransferMoney implements Runnable {
    private Bank bank;
    private int fromAcc;
    private double maxAmount;
    private final int delay = 1000;

    public TransferMoney(Bank bank, int fromAcc, double maxAmount) {
        this.bank = bank;
        this.fromAcc = fromAcc;
        this.maxAmount = maxAmount;
    }

    @Override
    public void run() {
        try {
            Random random = new Random();
            while (true) {
                int toAcc = random.nextInt(bank.size());
                double amount = random.nextDouble() * maxAmount;
                bank.transfer(fromAcc, toAcc, amount);
                Thread.sleep(random.nextInt(delay));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class SynchBank {
    public static void main(String[] args) {
        Bank bank = new Bank(100, 1000);
        for (int i = 0; i < bank.size(); i++) {
            TransferMoney transferMoney = new TransferMoney(bank, i, 100);
            Thread thread = new Thread(transferMoney);
            thread.start();
        }
    }
}
