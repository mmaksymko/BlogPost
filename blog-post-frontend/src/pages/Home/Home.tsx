import React, { useEffect, useState, useContext } from 'react';
import './Home.css';
import { PostResponse, SignedPost } from '../../models/Post';
import Posts from '../../components/Posts';
import { SnackBarContext, defaultSnackBar, Severity } from '../../contexts/SnackBarContext';
import { getPosts } from '../../api-calls/Post';
import { getUser } from '../../api-calls/User';

const Home: React.FC = () => {
    const [posts, setPosts] = useState<SignedPost[]>([]);
    const [last, setLast] = useState(false);
    const [page, setPage] = useState(0);

    const { setSnackBar } = useContext(SnackBarContext);
    const openSnack = (severity: Severity, message: string) => {
        setSnackBar({ ...defaultSnackBar, open: true, severity: severity, message: message });
    }

    const fetchAuthorName = (authorId: number) => {
        const onSuccess = (response: any) => response.data.firstName + " " + response.data.lastName;
        const onError = () => "Unknown"

        return getUser(authorId, onSuccess, onError);
    }

    const fetchPosts = async () => {
        const data = await getPosts(page, openSnack);

        const promises = data.content.map(async (result: PostResponse) => {
            const authorName = await fetchAuthorName(result.authorId);
            return {
                ...result,
                postedAt: new Date(`${result.postedAt}Z`),
                authorName
            };
        });
        const postsWithDateObjects = await Promise.all(promises);

        setPosts(prevPosts => [...prevPosts, ...postsWithDateObjects]);
        setLast(data.last);
        setPage(data.pageable.pageNumber + 1);
    }

    useEffect(() => {
        fetchPosts();
    }, []);
    return (
        <div className="home">
            <Posts posts={posts} />
            {!last && <div className="home-load load-more-posts" onClick={fetchPosts}>Load more...</div>}
        </div>
    );
};

export default Home;