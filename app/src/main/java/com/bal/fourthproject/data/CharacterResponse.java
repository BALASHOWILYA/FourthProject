package com.bal.fourthproject.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CharacterResponse
{
    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    private Info info;

    @SerializedName("results")
    private List<Character> characters;

    public List<Character> getCharacters(){
        return characters;
    }

    public  static class Info{
        private int count;
        private int pages;
        private String next;
        private String prev;

        public int getCount() {
            return count;
        }

        public int getPages() {
            return pages;
        }

        public String getNext() {
            return next;
        }

        public String getPrev() {
            return prev;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public void setPrev(String prev) {
            this.prev = prev;
        }
    }

}
