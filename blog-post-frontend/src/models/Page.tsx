
export interface Page<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            sorted: boolean;
            unsorted: boolean;
            empty: boolean;
        };
        offset: number;
        unpaged: boolean;
        paged: boolean;
    };
    totalPages: number;
    totalElements: number;
    last: boolean;
    numberOfElements: number;
    size: number;
    number: number;
    sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
    };
    first: boolean;
    empty: boolean;
}

export const emptyPage: Page<any> = {
    content: [],
    pageable: {
        pageNumber: 0,
        pageSize: 20,
        sort: {
            unsorted: true,
            sorted: false,
            empty: true
        },
        offset: 0,
        unpaged: false,
        paged: true
    },
    totalPages: 0,
    totalElements: 0,
    last: true,
    numberOfElements: 0,
    size: 20,
    number: 0,
    sort: {
        unsorted: true,
        sorted: false,
        empty: true
    },
    first: true,
    empty: true
}