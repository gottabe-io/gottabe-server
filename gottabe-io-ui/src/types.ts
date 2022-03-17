
export interface MenuItem {
    label: string;
    url: string;
    icon: string;
};

export interface UserData {

	id?: string;

	name?: string;

	lastName?: string;

	email?: string;

	image?: string;

};

export interface UserProfile {
    email?: string;
    name?: string;
    lastName?: string;
    description?: string;
    birthDate?: string;
    nickname?: string;
    githubAccount?: string;
    twitterAccount?: string;
}

export interface UserPrivacy {
    showEmail?: boolean;
    showTwitter?: boolean;
    showGithub?: boolean;
    showName?: boolean;
}

export interface ManagedToken {
    token: string;
    expireDate: string;
    id: string;
}


export interface Match {

	id: number;

    startTime: string;

    endTime: string;

    width: number;

    height: number;

    mines: number;

    minesDiscovered: number;

    cleared: number;

    status: string;

    data: string;

};
