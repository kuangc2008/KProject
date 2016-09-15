package com.kc.a50_android_hacks.aidl;

import android.os.RemoteException;

import com.kc.a50_android_hacks.IBookManager;

import java.util.List;

/**
 * Created by chengkuang on 16/9/15.
 */
public class BookManager {
    private List<Book> mBookList;

    private final IBookManager.Stub mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            synchronized (mBookList) {
                return mBookList;
            }
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (mBookList) {
                mBookList.add(book);
            }
        }
    };
}
