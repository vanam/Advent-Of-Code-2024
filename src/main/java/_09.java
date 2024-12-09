import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

class Memory {
    NavigableSet<Block> free = new TreeSet<>();
    NavigableSet<Block> used = new TreeSet<>();

    void add(Block block) {
        if (block.fileId == null) {
            free.add(block);
        } else {
            used.add(block);
        }
    }

    long checksum() {
        long checksum = 0;
        for (Block block : blocks()) {
            checksum += block.checksum();
        }
        return checksum;
    }

    NavigableSet<Block> blocks() {
        NavigableSet<Block> all = new TreeSet<>();
        all.addAll(free);
        all.addAll(used);
        return all;
    }

    void fragment() {
        List<Block> usedBlocks = new ArrayList<>(used.reversed());
        for (Block movingBlock : usedBlocks) {
            while (movingBlock.size > 0) {
                // Find the first free block before file itself
                Block freeBlock = null;
                if (free.first().blockStart <= movingBlock.blockStart) {
                    freeBlock = free.first();
                }

                if (freeBlock != null) {
                    // Remove free block if used fully
                    if (freeBlock.size <= movingBlock.size) {
                        free.remove(freeBlock);
                    }

                    // Create new free block in place of moving file
                    // Note: We don't care that there are multiple consecutive free blocks at the end
                    // Note: Unnecessary for AoC
                    int freeBlockSize = Math.min(freeBlock.size, movingBlock.size);
                    int freeBlockStart = movingBlock.blockStart + movingBlock.size - freeBlockSize;
                    free.add(new Block(freeBlockStart, freeBlockSize, null));

                    // Create file fragment
                    used.add(new Block(freeBlock.blockStart, freeBlockSize, movingBlock.fileId));

                    // Alter file block size
                    movingBlock.size -= freeBlockSize;

                    if (movingBlock.size == 0) {
                        used.remove(movingBlock);
                    }

                    // Alter free block size
                    freeBlock.blockStart += freeBlockSize;
                    freeBlock.size -= freeBlockSize;
                } else {
                    break; // nowhere to move file
                }
            }
        }
    }

    void compact() {
        List<Block> usedBlocks = new ArrayList<>(used.reversed());
        for (Block movingBlock : usedBlocks) {
            // Find the first large enough free block before file itself
            Block freeBlock = null;
            for (Block f : free) {
                if (f.blockStart > movingBlock.blockStart) {
                    break;
                } else if (f.size >= movingBlock.size) {
                    freeBlock = f;
                    break;
                }
            }

            if (freeBlock != null) {
                // Remove free block if used fully
                if (freeBlock.size == movingBlock.size) {
                    free.remove(freeBlock);
                }

                // Create new free block in place of moving file
                // Note: We don't care that there are multiple consecutive free blocks at the end
                // Note: Unnecessary for AoC
                free.add(new Block(movingBlock.blockStart, movingBlock.size, null));

                // Move file to the free block
                movingBlock.blockStart = freeBlock.blockStart;

                // Alter free block size
                freeBlock.blockStart += movingBlock.size;
                freeBlock.size -= movingBlock.size;
            }
        }
    }

    /** Use this only for example input */
    String toMemoryString() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blocks()) {
            sb.append(block.toMemoryString());
        }
        return sb.toString();
    }
}

class Block implements Comparable<Block> {
    int blockStart;
    int size;
    Integer fileId;

    Block(int blockStart, int size, Integer fileId) {
        this.blockStart = blockStart;
        this.size = size;
        this.fileId = fileId;
    }

    @Override
    public int compareTo(Block o) {
        return Integer.compare(this.blockStart, o.blockStart);
    }

    long checksum() {
        if (fileId == null) {
            return 0;
        }

        long checksum = 0;
        for (int i = 0; i < size; i++) {
            checksum += (blockStart + i) * fileId;
        }
        return checksum;
    }

    /** Use this only for example input */
    String toMemoryString() {
        char c = fileId != null ? (char) ('0' + fileId) : '.';
        return String.valueOf(c).repeat(Math.max(0, size));
    }
}

void main() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("09.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        String line = reader.readLine();
        Memory memory1 = new Memory();
        Memory memory2 = new Memory();
        int blockStart = 0;
        for (int i = 0; i < line.length(); i++) {
            int size = line.charAt(i) - '0';
            if (size == 0) {
                continue;
            }

            Integer fileId = i % 2 == 0 ? i /2 : null;
            memory1.add(new Block(blockStart, size, fileId));
            memory2.add(new Block(blockStart, size, fileId));

            blockStart += size;
        }

        memory1.fragment();
        System.out.println(memory1.checksum());

        memory2.compact();
        System.out.println(memory2.checksum());
    }
}
