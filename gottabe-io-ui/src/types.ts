
export interface MenuItem {
    label: string;
    url: string;
    icon: string;
};

export interface Owner {

    id: string;

    name: string;

    email: string;

    nickname: string;

    githubAccount: string;

    twitterAccount: string;

    description: string;

    createTime: string;

}

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
    creationDate: string;
}


export interface PackageGroup {

    name?: string;

    description?: string;

    owner?: Owner;

}

export interface PackageRelease {

    version: string;

    releaseDate: string;

    description: string;

    sourceUrl: string;

    issuesUrl: string;

    documentationUrl: string;

}

export interface PackageData {

    name: string;

    group: PackageGroup;

    releases?: PackageRelease[];

}
