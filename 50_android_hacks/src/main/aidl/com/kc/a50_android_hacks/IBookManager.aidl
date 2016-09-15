package com.kc.a50_android_hacks;

import com.kc.a50_android_hacks.aidl.Book;
interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}