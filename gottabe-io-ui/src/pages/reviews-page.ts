import {LitElement, html, customElement, css, property, unsafeCSS} from 'lit-element';
import {style} from '../styles';
import {packageService} from "../services/package-services";
import {reviewsService} from "../services/review-services";
import {NewReviewVO, PackageDataVO, PackageReleaseVO, ReviewVO} from "../types";
import {formatDateAgo, setValue} from '../util/utils';
import {exceptional} from '../util/exceptional';
import '../components/md-view';
import '../components/my-dropdown-menu';
import '../components/my-dialog';
import sourceIcon from 'url:~/resources/source_icon.svg';
import docsIcon from 'url:~/resources/docs_icon_18x18.png';
import issuesIcon from 'url:~/resources/issues_icon.png';
import starsOn from 'url:~/resources/stars_on.png';
import starsOff from 'url:~/resources/stars_off.png';
import likeOn from 'url:~/resources/like_on.svg';
import likeOff from 'url:~/resources/like.svg';
import dislikeOn from 'url:~/resources/dislike_on.svg';
import dislikeOff from 'url:~/resources/dislike.svg';
import {MyDialogElement} from "../components/my-dialog";

@customElement("reviews-page")
@exceptional
class ReviewsPage extends LitElement {

    static get styles() {
        return [style,css`
            :host {
                display: block;
                align-items: center;
                width: 100%;
                color: aliceblue;
            }

            table {
                width: 100%;
                border: none;
            }

            th {
                background: #585c64;
                border: none;
            }

            td {
                background: #383c44;
                border: none;
            }

            td.center {
                text-align: center;
            }

            a {
                color: white;
                text-decoration: none;
                font-weight: bold;
            }

            a:hover {
                color: yellow;
            }

            .buttons {
                display: flex;
            }

            .form-field + .form-field {
                margin-top: 8px;
            }

            #package_view {
                display: block;
                background: #fff;
                color: #24292f;
            }

            div.centered {
                display: flex;
                flex-wrap: nowrap;
                flex-direction: row;
                margin-left: auto;
                margin-right: auto;
                width: 75em;
            }

            div.centered2 {
                display: flex;
                flex-wrap: wrap;
                flex-direction: row;
                margin-left: auto;
                margin-right: auto;
                width: 75em;
            }

            #package_head {
                width: 50em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            dropdown-menu {
                width: 25em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            #package_view div.centered md-view {
                width: 50em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            #package_view div.centered .details {
                width: 25em;
                margin-left: 1rem;
                margin-right: 1rem;
            }

            #package_view label {
                color: #555;
                font-weight: bold;
                margin-top: 1.5em;
                margin-bottom: 0.5em;
            }

            #package_view a {
                color: #222;
                font-weight: bold;
            }

            #package_view h5, #package_view h6 {
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                font-size: 1em;
                margin-top: 1em;
                margin-bottom: 1em;
            }

            #package_view h5, #package_view h5 a {
                font-family: monospace;
                font-weight: bold;
                color: #eee;
                height: 18px;
            }

            #package_view h5 {
                margin-top: 0px;
                margin-bottom: 0px;
                padding-top: 1em;
                padding-left: 0.5em;
                padding-bottom: 1em;
                background-color: #282c34;
                border: 1px solid #383c44;
                border-radius: 6px;
            }

            #package_view h5 img.copyIt {
                float: right;
                display: none;
                width: 18px;
                height: 18px;
            }

            #package_view h5:hover {
                background-color: #383c48;
                border: 1px solid #585c74;
            }

            #package_view h5:hover img.copyIt {
                display: block;
            }

            #package_view h6 {
                color: #262a32;
            }

            .repository, .documentation, .issues {
                padding-left: 18px;
                background-size: 18px 18px;
                background-repeat: no-repeat;
                background-position: 0px 0px;
            }

            .repository {
                background-image: url(${unsafeCSS(sourceIcon)});
            }

            .documentation {
                background-image: url(${unsafeCSS(docsIcon)});
            }

            .issues {
                background-image: url(${unsafeCSS(issuesIcon)});
            }

            ul.stars {
                display: flex;
                flex-wrap: nowrap;
                align-items: stretch;
                padding: 0px;
                margin: 0px;
            }

            ul.stars li {
                text-decoration: none;
                list-style-type: none;
                height: 18px;
            }

            ul.stars li.star {
                background-image: url(${unsafeCSS(starsOff)});
                width: 90px;
                height: 18px;
                margin-right: 4px;
            }

            ul.stars li img {
                width: 18px;
                height: 18px;
                padding: 0;
                margin: 0 4px 0 8px;
                border: 0;
            }

            ul.stars li.title {
                font-weight: bold;
                flex: 1 1 auto;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            ul.stars li.star span {
                height: 18px;
                background-image: url(${unsafeCSS(starsOn)});
                display: block;
            }

            .review {
                border-radius: 6px;
                background-color: #aabbcc;
                padding: 8px;
                margin: 4px;
            }

            .unformatted {
                max-height: 48px;
                overflow: hidden;
                text-overflow: ellipsis;
            }

            .load-more {
                background-color: #99aadd;
                border-radius: 6px;
                border: 1px solid #8899cc;
                color: #282c34;
                width: 33%;
                padding: 4px;
                margin-left: auto;
                margin-right: auto;
            }
        `];
    }

    @property()
    private error?: string;

    @property({attribute: true})
    groupName?: string;

    @property({attribute: true})
    packageName?: string;

    @property({attribute: true})
    version?: string;

    private _package?: PackageDataVO;

    private _selectedVersion : PackageReleaseVO | undefined;

    private _reviews: ReviewVO[];

    private _page: number;

    private _totalPages: number;

    private _review: NewReviewVO | undefined;

    private errorFields: any;

    render() {
        return html`
			<div>
                <div class="error">
                    <h3>${this.error}</h3>
                </div>
            ${ this._package ? html`
                <div>
                    <div class="centered">
                        <div id="package_head">
                            <h2>${this.groupName}/${this.packageName}</h2>
                            <h3>${this.latestVersion 
                                    ? ('Latest version ' + this.latestVersion.version + ' released ' + formatDateAgo(this.latestVersion.releaseDate) + ' ago') 
                                    : 'No versions released yet!'}</h3>
                        </div>
                        <dropdown-menu title="Version" .options="${this._package?.releases}" .value="${this._selectedVersion}" labelProperty="version" @change="${this._handleChangeVersion}"></dropdown-menu>
                    </div>
                </div>
                ${this._renderReviews()}
            ` : html`
                <h2>Not found!</h2>
                <h3>The package you are looking for may not exist!</h3>
            `}
            </div>
            <my-dialog id="createReviewDlg" showCancel="true">
                <div slot="caption">Create Review</div>
                <div>
                    <div class="form-field">
                        <label>Title:</label>
                        <span class="error">
                            <h3>${this.errorFields?.name}</h3>
                        </span>
                        <input type="text" name="_review.title" .value="${this._review?.title}" @change="${this._handleChange}"/>
                    </div>
                    <div class="row">
                        <div class="col-2">
                            <label>Rate:</label>
                            <span class="error">
                                <h3>${this.errorFields?.rate}</h3>
                            </span>
                            <input type="number" name="_review.rate" .value="${this._review?.rate}" @change="${this._handleChange}" max="5" min="0">
                        </div>
                        <div class="col-2">
                            <label>
                                Vulnerability:
                            </label>
                            <span class="error">
                                <h3>${this.errorFields?.vulnerability}</h3>
                            </span>
                            <label class="checkbox">
                                <input type="checkbox" name="_review.vulnerability" .checked="${this._review?.vulnerability}" @change="${this._handleChange}" value="true">
                                Is Vulnerability
                            </label>
                        </div>
                    </div>
                    <div class="form-field">
                        <label>Review:</label>
                        <span class="error">
                            <h3>${this.errorFields?.review}</h3>
                        </span>
                        <textarea rows="5" name="_review.review" .value="${this._review?.review}" @change="${this._handleChange}">
                        </textarea>
                    </div>
                </div>
            </my-dialog>
		`;
    }

    private _renderReviews() {
        return html`
            <div id="package_view">
                <div class="centered2">
                    <div style="width: 100%; margin-bottom: 4px;margin-top: 8px;">
                        <button type="button" class="load-more" @click="${this._createReview}">Create a review</button>
                    </div>
                    <div>
                        ${this._reviews?.map((review: ReviewVO) => html`
                            <div class="review unformatted">
                                ${this._getStars(review)}
                                ${review.review}
                            </div>
                        `)}
                    </div>
                    <div style="width: 100%; margin-bottom: 4px; display: ${this._totalPages > this._page ? 'block' : 'none'}"><button type="button" class="load-more" @click="${this._loadMore}">Load more</button></div>
                </div>
            </div>
        `;
    }

    constructor() {
        super();
        this._page = 0;
        this._totalPages = 0;
        this._reviews = [];
    }

    connectedCallback() {
        super.connectedCallback();
        this._page = 0;
        this._updateData().catch(((e:any) => this.error = e.message).bind(this));
    }

    private async _updateData() {
        if (this.groupName && this.packageName) {
            this._package = await packageService.packageInfo(this.groupName, this.packageName, undefined, 'ALL_RELEASES');
            this._selectedVersion = this._package.releases?.filter(release => release.version == this.version)[0];
            if (this._selectedVersion && this._selectedVersion.version) {
                if (this.groupName && this.packageName && this._selectedVersion && this._selectedVersion.version) {
                    let resp: Response = await reviewsService.findReviews(this.groupName, this.packageName, this._selectedVersion.version, this._page, 10, {acceptType: '*'});
                    this._reviews = await resp.json();
                    this._page = this._reviews.length;
                    this._totalPages = parseInt(resp.headers.get('RESULT_COUNT') || '0');
                }
            }
        }
        await this.performUpdate();
    }

    private async _loadMore() {
        if (this.groupName && this.packageName && this._selectedVersion && this._selectedVersion.version) {
            let items = await reviewsService.findReviews(this.groupName, this.packageName, this._selectedVersion.version, this._page, 10);
            if (items && items.length > 0) {
                this._page += items.length;
                this._reviews?.push(...items);
                await this.performUpdate();
            }
        }
    }

    private get latestVersion() : PackageReleaseVO | undefined {
        let releases = this._package?.releases;
        if (releases && releases.length) {
            return releases[releases.length - 1];
        }
        return undefined;
    }

    private _handleChangeVersion(e: CustomEvent) {
        this._selectedVersion = e.detail;
        this.performUpdate();
    }

    private _getStars(review: ReviewVO) {
        let st = review.rate || 0;
        let sw = Math.round(st * 90 / 5);
        review.myRate = review.myRate || 0;
        let liked = review.myRate > 0;
        let disliked = review.myRate < 0;
        return html`
            <ul class="stars">
                <li class="star" title="${st} stars"><span style="width: ${sw}px"> </span></li>
                <li class="title">${review.title}</li>
                <li><img src="${liked? likeOn : likeOff}" @click="${this._likeReview}" .model="${review}" .like="${true}"></li>
                <li>${review.reviewLikes}</li>
                <li><img src="${disliked ? dislikeOn : dislikeOff}" @click="${this._likeReview}" .model="${review}" .like="${false}"></li>
                <li>${review.reviewDislikes}</li>
            </ul>
        `;
    }

    private async _likeReview(e: any) {
        let review: ReviewVO = e.path[0].model;
        let like = e.path[0].like;
        if (review.myRate != (like ? 1 : -1)) {
            await (<any>this).tryCatchAsync((async () => {
                if (this.groupName && this.packageName && this._selectedVersion && this._selectedVersion.version && review.id) {
                    await reviewsService.rateReview(this.groupName, this.packageName, this._selectedVersion.version, review.id, {rate: like ? 1 : -1});
                    if ((review.myRate || 0) > 0)
                        review.reviewLikes = review.reviewLikes || 0 - 1;
                    if ((review.myRate || 0) < 0)
                        review.reviewDislikes = review.reviewDislikes || 0 - 1;
                    review.myRate = like ? 1 : -1;
                    if ((review.myRate || 0) > 0)
                        review.reviewLikes = review.reviewLikes || 0 + 1;
                    if ((review.myRate || 0) < 0)
                        review.reviewDislikes = review.reviewDislikes || 0 + 1;
                    this.performUpdate();
                }
            }).bind(this));
        }
    }

    private async _createReview() {
        let createReviewDlg = <MyDialogElement>this.shadowRoot?.querySelector('my-dialog#createReviewDlg');
        this._review = {title: '', review: '', rate: 0, vulnerability: false};
        (<any>this).clearErrors();
        await this.performUpdate();
        let result: string;
        do {
            result = await createReviewDlg?.show(true);
            if (result == 'ok') {
                try {
                    this.groupName && this.packageName && this.version &&
                    await reviewsService.createReview(this.groupName, this.packageName, this.version, this._review);
                } catch (e: any) {
                    await (<any>this).processError(e);
                    result = 'error';
                }
            }
        } while(result == 'error');
    }

    private _handleChange(e:any) {
        let target = e.target;
        if (target) {
            setValue(this,target.name, target.type == 'checkbox' || target.type == 'radio' ? (target.checked ? target.value : null) : target.value);
            this.errorFields = {};
        }
    }

}

declare global {
    interface HTMLElementTagNameMap {
        'reviews-page': ReviewsPage;
    }
}
