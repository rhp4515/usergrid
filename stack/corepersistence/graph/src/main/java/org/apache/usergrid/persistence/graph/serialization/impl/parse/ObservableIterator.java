package org.apache.usergrid.persistence.graph.serialization.impl.parse;


import java.util.Iterator;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;


/**
 * Converts an iterator to an observable.  Subclasses need to only implement getting the iterator from the data source.
 * This is used in favor of "Observable.just" when the initial fetch of the iterator will require I/O.  This allows
 * us to wrap the iterator in a deferred invocation to avoid the blocking on construction.
 */
public abstract class ObservableIterator<T> implements Observable.OnSubscribe<T> {


    @Override
    public void call( final Subscriber<? super T> subscriber ) {


        try {
            //get our iterator and push data to the observer
            Iterator<T> itr = getIterator();


            //while we have items to emit and our subscriber is subscribed, we want to keep emitting items
            while ( itr.hasNext() && !subscriber.isUnsubscribed()) {
                subscriber.onNext( itr.next() );
            }

            subscriber.onCompleted();
        }

        //if any error occurs, we need to notify the observer so it can perform it's own error handling
        catch ( Throwable t ) {
            subscriber.onError( t );
        }

    }


    /**
     * Return the iterator to feed data to
     */
    protected abstract Iterator<T> getIterator();
}