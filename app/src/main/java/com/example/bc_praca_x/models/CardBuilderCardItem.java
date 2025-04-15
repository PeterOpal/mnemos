package com.example.bc_praca_x.models;

import com.example.bc_praca_x.database.enums.CardType;

import java.util.ArrayList;
import java.util.List;

public class CardBuilderCardItem {
    public Long id;

    public CardBuilderCardItem(Long id) {
        this.id = id;
    }

    public static class Block {
        public Long id;
        public CardType type;
        public String content;

        public Block(CardType type, String content, Long id) {
            this.id = id;
            this.type = type;
            this.content = content;
        }
    }

    private List<Block> frontBlocks;
    private List<Block> backBlocks;

    public CardBuilderCardItem() {
        this.frontBlocks = new ArrayList<>();
        this.backBlocks = new ArrayList<>();
    }

    public void setBackBlocks(List<Block> backBlocks) {
        this.backBlocks = backBlocks;
    }

    public void setFrontBlocks(List<Block> frontBlocks) {
        this.frontBlocks = frontBlocks;
    }

    public List<Block> getBlocks(boolean isFront) {
        return isFront ? frontBlocks : backBlocks;
    }

    public boolean canAddBlock(CardType type, boolean isFront) {
        List<Block> blocks = isFront ? frontBlocks : backBlocks;
        return blocks.size() < 3 && !containsBlockType(blocks, type);
    }

    public void addBlock(CardType type, String content, boolean isFront) {
        if (canAddBlock(type, isFront)) {
            getBlocks(isFront).add(new Block(type, content, null));
        }
    }

    public void removeBlock(CardType type, boolean isFront) {
        getBlocks(isFront).removeIf(block -> block.type == type);
    }

    private boolean containsBlockType(List<Block> blocks, CardType type) {
        for (Block block : blocks) {
            if (block.type == type) return true;
        }
        return false;
    }

    public boolean isFrontEmpty() {
        return frontBlocks.isEmpty();
    }

    public boolean isBackEmpty() {
        return backBlocks.isEmpty();
    }

    public boolean validateCard() {
        return isFrontEmpty() || isBackEmpty();
    }
}
