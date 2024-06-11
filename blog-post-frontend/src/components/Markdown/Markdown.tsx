import React from 'react';
import ReactMarkdown from 'react-markdown';
import remarkMath from 'remark-math';
import rehypeKatex from 'rehype-katex'
import rehypeRaw from 'rehype-raw';
import rehypeSanitize, { defaultSchema } from 'rehype-sanitize';
import rehypeHighlight from 'rehype-highlight';
import 'katex/dist/katex.min.css';
import 'highlight.js/styles/github-dark.css';
import './Markdown.css';


interface MarkdownProps {
    content?: string;
    className?: string;
}

const Markdown: React.FC<MarkdownProps> = ({ className, content }) => {

    const sanitizeSchema = {
        ...defaultSchema,
        attributes: {
            ...defaultSchema.attributes,
            '*': [...(defaultSchema.attributes?.['*'] || []), 'style'],
        },
    };

    return (
        <ReactMarkdown
            className={`${className ? className + ' ' : ''}markdown`}
            remarkPlugins={[remarkMath]}
            rehypePlugins={[rehypeRaw, [rehypeSanitize, sanitizeSchema], [rehypeKatex, { strict: false }], rehypeHighlight]}
        >
            {content}
        </ReactMarkdown >
    );
};

export default Markdown;