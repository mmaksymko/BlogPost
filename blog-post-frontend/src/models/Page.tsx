
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