import React, { useEffect, useState, useContext, useRef } from 'react';
import './Profile.css';
import { PostResponse, SignedPost } from '../../models/Post';
import Posts from '../../components/Posts';
import { SnackBarContext, defaultSnackBar, Severity } from '../../contexts/SnackBarContext';
import { getPosts } from '../../api-calls/Post';
import { changePfp, editUser, getUser, logout } from '../../api-calls/User';
import { AuthContext } from '../../contexts/AuthContext';
import { redirect, useNavigate, useParams } from 'react-router-dom';
import Button from '../../components/Button';
import { UserResponse } from '../../models/User';
import CommentBox from '../../components/CommentBox';
import { defaultPfp } from '../../config';

const Profile: React.FC = () => {
    const { id, setUser: setAuthorizedUser } = useContext(AuthContext);
    const { userId: paramUserId } = useParams<{ userId: string }>();
    const [userId, setUserId] = useState<number>(-1);
    const [user, setUser] = useState<UserResponse>();

    const [isEditMenuOpen, setIsEditMenuOpen] = useState(false);

    const fileInputRef = useRef<HTMLInputElement>(null);
    const [headerImage, setHeaderImage] = useState<File | null>(null);
    const [headerImageUrl, setHeaderImageUrl] = useState<string | null>(null);
    const [imageSrc, setImageSrc] = useState<string | undefined>('https://lh3.googleusercontent.com/a/ACg8ocIkQuLkcTAvp_LoVBrAvBEPD92fwTSamT7JjcW5DHTrfo5fWRt1=s96-c');
    const [firstName, setFirstName] = useState<string | undefined>('');
    const [lastName, setLastName] = useState<string | undefined>('');
    const [email, setEmail] = useState<string | undefined>('');

    const navigate = useNavigate();
    useEffect(() => {
        const identificator = paramUserId ? parseInt(paramUserId) : id;
        if (!identificator) {
            navigate('/login')
            return
        }
        setUserId(identificator);
        fetchPosts(identificator);
    }, [])

    const [posts, setPosts] = useState<SignedPost[]>([]);
    const [last, setLast] = useState(false);
    const [page, setPage] = useState(0);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    const fetchUser = (authorId: number): Promise<UserResponse> => {
        const onSuccess = (response: any) => response.data;
        const onError = () => null

        return getUser(authorId, onSuccess, onError);
    }

    const fetchPosts = async (identificator: number) => {
        const fetchedUser = await fetchUser(identificator);
        if (!fetchedUser) {
            navigate('/no-page')
            return
        }

        setUser(fetchedUser)
        setImageSrc(fetchedUser.pfpUrl);
        setFirstName(fetchedUser.firstName);
        setLastName(fetchedUser.lastName);
        setEmail(fetchedUser.email);

        const data = await getPosts(page, openSnack, identificator);
        const postsWithDateObjects = data.content.map((result: PostResponse) => {
            return {
                ...result,
                postedAt: new Date(`${result.postedAt}Z`),
                authorName: `${fetchedUser.firstName} ${fetchedUser.lastName}`
            };
        });

        setPosts(prevPosts => [...prevPosts, ...postsWithDateObjects]);
        setLast(data.last);
        setPage(data.pageable.pageNumber + 1);
    }

    const handleLogout = async () => {
        await logout();
        redirect('/')
    }

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            setHeaderImage(e.target.files[0]);
            setHeaderImageUrl(URL.createObjectURL(e.target.files[0]));
            setImageSrc(undefined);
        }
    }

    const handleUserEdit = async () => {
        let edited = false;
        if (headerImageUrl && headerImage) {
            const resultUser = await changePfp(userId, headerImage, openSnack);
            if (resultUser) {
                setAuthorizedUser(prevState => ({
                    ...prevState,
                    pfpUrl: resultUser.pfpUrl
                }));
                edited = true;
            }
        }
        if (firstName && lastName && email && user &&
            (email !== user.email || firstName !== user.firstName || lastName !== user.lastName)) {
            const resultUser = await editUser(userId, firstName, lastName, email, openSnack);
            if (resultUser) {
                setAuthorizedUser(prevState => ({
                    ...prevState,
                    firstName: resultUser.firstName,
                    lastName: resultUser.lastName,
                    email: resultUser.email
                }));
                edited = true;
            }
        }
        if (edited) {
            window.location.reload();
        }
    }

    return (
        <>
            {
                id !== userId &&
                <div className='user-info-profile change-pfp-container select-post-header-image'>
                    <div className='change-pfp'>
                        <div className='create-post-section-title'>
                            {user?.firstName} {user?.lastName}
                        </div>
                    </div>
                    <img
                        className='pfp-preview post-header-image-preview'
                        src={user?.pfpUrl || defaultPfp}
                        alt="Header preview"
                    />
                </div>
            }
            {
                id === userId &&
                <div className="management-panel">
                    <Button inverted onClick={() => setIsEditMenuOpen(!isEditMenuOpen)}>
                        Редагувати
                    </Button>
                    <Button inverted onClick={handleLogout}>
                        Вийти
                    </Button>
                </div>
            }
            {
                isEditMenuOpen &&
                <div>
                    <input
                        ref={fileInputRef}
                        accept="image/*"
                        style={{ display: 'none' }}
                        id="raised-button-file"
                        type="file"
                        onChange={handleFileChange}
                    />
                    <div className='change-pfp-container select-post-header-image'>
                        <div className='change-pfp'>
                            <div className='create-post-section-title'>
                                Фото профілю:
                            </div>
                            <Button width='8rem' height='3rem' inverted onClick={() => fileInputRef.current?.click()}>
                                Обрати
                            </Button>
                        </div>
                        {(imageSrc || headerImageUrl) &&
                            <img
                                className='pfp-preview post-header-image-preview'
                                src={imageSrc || headerImageUrl || ''}
                                alt="Header preview"
                            />
                        }
                    </div>
                    <div className='change-user-info-container'>
                        <div className="change-user-info">
                            <CommentBox
                                placeholder="Ім'я"
                                maxLength={64}
                                noArrow
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                            />
                            <CommentBox
                                placeholder='Прізвище'
                                maxLength={64}
                                noArrow
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                            />
                            <CommentBox
                                placeholder='Електронна пошта'
                                maxLength={256}
                                noArrow
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="save-edited-user">
                        <Button width='10rem' height='3rem' inverted onClick={handleUserEdit}>
                            Зберегти
                        </Button>
                    </div>
                    <hr />

                    <section className="change-user-info-section">

                    </section>
                </div>
            }
            <div className="profile-posts">
                <Posts posts={posts} />
                {!last && <div className="home-load load-more-posts" onClick={() => fetchPosts(userId)}>Load more...</div>}
            </div>
        </>
    );
};

export default Profile;