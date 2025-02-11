import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

class Block {
    private int index;
    private String timestamp;
    private String transactionData;
    private String previousHash;
    private String currentHash;

    public Block(int index, String timestamp, String transactionData, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactionData = transactionData;
        this.previousHash = previousHash;
        this.currentHash = calculateHash();
    }

    public String calculateHash() {
        String data = index + timestamp + transactionData + previousHash;
        return applySHA256(data);
    }

    private String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getIndex() {
        return index;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

class Blockchain {
    private List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        // Create the first block (genesis block)
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(0, "2025-02-11 12:00:00", "Genesis Block", "0");
    }

    public void addBlock(String transactionData) {
        Block previousBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(previousBlock.getIndex() + 1,
                                   "2025-02-11 12:30:00", // For simplicity, use a fixed timestamp
                                   transactionData,
                                   previousBlock.getCurrentHash());
        chain.add(newBlock);
    }

    public boolean isValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Check if current block's previous hash matches the previous block's hash
            if (!currentBlock.getPreviousHash().equals(previousBlock.getCurrentHash())) {
                return false;
            }

            // Check if current block's hash is valid
            if (!currentBlock.getCurrentHash().equals(currentBlock.calculateHash())) {
                return false;
            }
        }
        return true;
    }

    public void printChain() {
        for (Block block : chain) {
            System.out.println("Block " + block.getIndex());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Transaction Data: " + block.getTransactionData());
            System.out.println("Hash of Previous Block: " + block.getPreviousHash());
            System.out.println("Current Block Hash: " + block.getCurrentHash());
            System.out.println("----------------------");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();

        // Add some blocks with transaction data
        blockchain.addBlock("Transaction 1: Alice sends 10 BTC to Bob");
        blockchain.addBlock("Transaction 2: Bob sends 5 BTC to Charlie");
        blockchain.addBlock("Transaction 3: Charlie sends 2 BTC to Dave");

        // Print the blockchain
        blockchain.printChain();

        // Verify if the blockchain is valid
        System.out.println("Is the blockchain valid? " + blockchain.isValid());
    }
}
