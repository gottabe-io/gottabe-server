
export interface MenuItem {
    label: string;
    url: string;
    icon: string;
};

export interface ErrorFieldVO {

    id?: string;
    message: string;

}

export interface ErrorVO {

    message?: string;
    fields?: ErrorFieldVO[];
}

export * from 'gottabe-client';
