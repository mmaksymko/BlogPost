import React, { useEffect, useState } from 'react';
import './Home.css';
import axios from 'axios';
import { serverURL } from '../../config';
import { PostResponse, SignedPost } from '../../models/Post';
import Posts from '../../components/Posts';

const Home: React.FC = () => {
    const [posts, setPosts] = useState<SignedPost[]>([]);
    const [last, setLast] = useState(false);
    const [page, setPage] = useState(0);

    const fetchAuthorName = (authorId: number) => {
        return axios.get(`${serverURL}/users-service/users/${authorId}/`, { withCredentials: true }).then(response => {
            const data = response.data
            return data.firstName + " " + data.lastName;
        }).catch(error => {
            console.error(error);
            return "Unknown";
        });
    }

    const fetchPosts = async () => {
        const response = await axios.get(`${serverURL}/blog-post-service/posts/?size=10&page=${page}`, { withCredentials: true });
        const data = response.data;
        const promises = data.content.map(async (result: PostResponse) => {
            const authorName = await fetchAuthorName(result.authorId);
            return {
                ...result,
                postedAt: new Date(`${result.postedAt}Z`),
                authorName
            };
        });
        const postsWithDateObjects = await Promise.all(promises);
        console.log(postsWithDateObjects);

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
            {!last && <div className="load-more-posts" onClick={fetchPosts}>Load more...</div>}
        </div>
    );
};

export default Home;