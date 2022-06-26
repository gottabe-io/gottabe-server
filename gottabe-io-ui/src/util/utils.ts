
export const formatDatetime = (date?: string | number | Date) => {
    if (date) {
        let _date = typeof date == 'string' ? Date.parse(date) : date;
        return (_date instanceof Date ? _date : new Date(_date)).toLocaleString();
    }
    return '';
};

export const formatDate = (date?: string | number | Date) => {
    if (date) {
        let _date = typeof date == 'string' ? Date.parse(date) : date;
        return (_date instanceof Date ? _date : new Date(_date)).toLocaleDateString();
    }
    return '';
};

export const formatDateAgo = (date?: string | number | Date) => {
    if (date) {
        let _daten = typeof date == 'string' ? Date.parse(date) : date;
        let _date = (_daten instanceof Date ? _daten : new Date(_daten));
        let now = new Date();
        let dy = now.getFullYear() - _date.getFullYear();
        let dM = now.getMonth() - _date.getMonth();
        let dd = now.getDate() - _date.getDate();
        let dh = now.getHours() - _date.getHours();
        let dm = now.getMinutes() - _date.getMinutes();
        let ds = now.getSeconds() - _date.getSeconds();
        if (dy > 0) return dy + (dy > 1 ? ' years' : 'year');
        if (dM > 0) return dM + (dM > 1 ? ' months' : ' month');
        if (dd > 0) return dd + (dd > 1 ? ' days' : ' day');
        if (dh > 0) return dh + (dh > 1 ? ' hours' : ' hour');
        if (dm > 0) return dm + (dm > 1 ? ' minutes' : ' minute');
        if (ds > 0) return ds + (ds > 1 ? ' seconds' : ' second');
    }
    return '';
};

export function setValue(base: any, path: string, value: any) {
    let ids = path.split(".");
    let last = '';
    ids.forEach((id: string, i: number) => {
        if (i < ids.length - 1)
            base = base[id];
        else
            last = id;
    });
    base[last] = value;
}
