package com.hooksniff;

import java.util.*;
import java.util.function.Function;

/**
 * Pagination Helper for HookSniff Java SDK.
 *
 * Provides automatic cursor-based pagination for list() methods.
 *
 * Usage:
 *   // Auto-paginate through all items
 *   Paginator<MessageOut> paginator = Paginator.create(
 *       opts -> hs.getMessage().list(opts),
 *       new MessageListOptions().limit(100)
 *   );
 *   for (MessageOut msg : paginator) {
 *       System.out.println(msg.getId());
 *   }
 */
public class Paginator<T> implements Iterable<T> {

    private final Function<ListOptions, ListResponse<T>> fetchPage;
    private final Integer limit;
    private String iterator;

    public Paginator(Function<ListOptions, ListResponse<T>> fetchPage, Integer limit) {
        this.fetchPage = fetchPage;
        this.limit = limit;
    }

    public static <T> Paginator<T> create(
            Function<ListOptions, ListResponse<T>> fetchPage,
            Integer limit
    ) {
        return new Paginator<>(fetchPage, limit);
    }

    @Override
    public Iterator<T> iterator() {
        return new PaginatorIterator();
    }

    /**
     * Collect all items into a list.
     */
    public List<T> toList() {
        List<T> items = new ArrayList<>();
        for (T item : this) {
            items.add(item);
        }
        return items;
    }

    private class PaginatorIterator implements Iterator<T> {
        private ListResponse<T> currentPage;
        private int currentIndex;
        private boolean done;

        @Override
        public boolean hasNext() {
            if (done) return false;

            // Fetch first page if needed
            if (currentPage == null) {
                fetchNextPage();
                if (currentPage == null) return false;
            }

            // Check current page
            if (currentIndex < currentPage.getData().size()) {
                return true;
            }

            // Try next page
            if (!currentPage.isDone() && currentPage.getIterator() != null) {
                iterator = currentPage.getIterator();
                fetchNextPage();
                return currentPage != null && !currentPage.getData().isEmpty();
            }

            done = true;
            return false;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return currentPage.getData().get(currentIndex++);
        }

        private void fetchNextPage() {
            try {
                ListOptions opts = new ListOptions(limit, iterator);
                currentPage = fetchPage.apply(opts);
                currentIndex = 0;
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch page", e);
            }
        }
    }

    /**
     * Options for list requests.
     */
    public static class ListOptions {
        public final Integer limit;
        public final String iterator;

        public ListOptions(Integer limit, String iterator) {
            this.limit = limit;
            this.iterator = iterator;
        }
    }

    /**
     * Generic list response with pagination info.
     */
    public static class ListResponse<T> {
        private final List<T> data;
        private final boolean done;
        private final String iterator;

        public ListResponse(List<T> data, boolean done, String iterator) {
            this.data = data;
            this.done = done;
            this.iterator = iterator;
        }

        public List<T> getData() { return data; }
        public boolean isDone() { return done; }
        public String getIterator() { return iterator; }
    }
}
