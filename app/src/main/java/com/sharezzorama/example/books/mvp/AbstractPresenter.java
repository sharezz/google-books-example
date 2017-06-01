package com.sharezzorama.example.books.mvp;


import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class AbstractPresenter<T>{

    private final Set<WeakReference<T>> mViews;
    private final T mView;

    protected abstract Class<T> getViewClass();

    protected void onViewAttach(T view) {

    }

    protected void onViewDetach(T view) {

    }

    public AbstractPresenter() {
        mViews = new HashSet<>();
        mView = new ViewProxy().createView();
    }

    public void attachView(T view) {
        if (view != null && !checkViewAttach(view)) {
            mViews.add(new WeakReference<>(view));
            onViewAttach(view);
        }
    }

    public void detachView(T view) {
        for (Iterator<WeakReference<T>> i = mViews.iterator(); i.hasNext(); ) {
            WeakReference<T> item = i.next();
            if (item.get() == null || item.get().equals(view)) {
                i.remove();
                onViewDetach(view);
            }
        }
    }

    protected boolean checkViewAttach(T view) {
        for (WeakReference<T> reference : mViews) {
            if (reference.get() == view) {
                return true;
            }
        }
        return false;
    }

    protected T getView() {
        return mView;
    }

    class ViewProxy implements InvocationHandler {

        @SuppressWarnings("unchecked")
        public T createView() {
            Class<T> viewClass = getViewClass();
            return (T) Proxy.newProxyInstance(viewClass.getClassLoader(),
                    new Class<?>[]{viewClass}, new ViewProxy());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Iterator<WeakReference<T>> i = mViews.iterator(); i.hasNext(); ) {
                WeakReference<T> item = i.next();
                if (item.get() != null) {
                    method.invoke(item.get(), args);
                } else {
                    i.remove();
                }
            }

            return null;
        }
    }
}
