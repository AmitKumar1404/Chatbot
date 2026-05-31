import { memo, useEffect, useMemo, useRef, useState } from "react";

const MARKDOWN_LINK_RE = /\[([^\]]+)\]\(([^)]+)\)/g;
const BOLD_RE = /\*\*([^*]+)\*\*/g;
const ITALIC_RE = /(^|[^\*])\*([^*\n]+)\*(?!\*)/g;
const INLINE_CODE_RE = /`([^`]+)`/g;

function escapeHtml(text) {
  return (text ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function sanitizeHref(rawHref) {
  const href = (rawHref ?? "").trim();
  if (!href) return null;
  if (
    href.startsWith("http://") ||
    href.startsWith("https://") ||
    href.startsWith("mailto:")
  ) {
    return href;
  }
  return null;
}

function applyInlineMarkdown(text) {
  let html = escapeHtml(text);

  html = html.replace(INLINE_CODE_RE, "<code>$1</code>");
  html = html.replace(BOLD_RE, "<strong>$1</strong>");
  html = html.replace(ITALIC_RE, "$1<em>$2</em>");
  html = html.replace(MARKDOWN_LINK_RE, (_, label, href) => {
    const safeHref = sanitizeHref(href);
    if (!safeHref) return escapeHtml(label);
    return `<a href="${escapeHtml(
      safeHref
    )}" target="_blank" rel="noreferrer noopener">${escapeHtml(label)}</a>`;
  });

  return html;
}

function renderMarkdownToHtml(markdownText) {
  const lines = (markdownText ?? "").split("\n");
  const htmlParts = [];
  let index = 0;

  while (index < lines.length) {
    const line = lines[index];

    if (!line.trim()) {
      index += 1;
      continue;
    }

    if (line.startsWith("```")) {
      const lang = line.slice(3).trim();
      index += 1;
      const codeLines = [];
      while (index < lines.length && !lines[index].startsWith("```")) {
        codeLines.push(lines[index]);
        index += 1;
      }
      if (index < lines.length && lines[index].startsWith("```")) {
        index += 1;
      }

      htmlParts.push(
        `<pre><code class="md-code${lang ? ` language-${escapeHtml(lang)}` : ""}">${escapeHtml(
          codeLines.join("\n")
        )}</code></pre>`
      );
      continue;
    }

    const headingMatch = line.match(/^(#{1,6})\s+(.*)$/);
    if (headingMatch) {
      const level = headingMatch[1].length;
      htmlParts.push(
        `<h${level}>${applyInlineMarkdown(headingMatch[2])}</h${level}>`
      );
      index += 1;
      continue;
    }

    if (/^\s*>\s+/.test(line)) {
      const quoteLines = [];
      while (index < lines.length && /^\s*>\s+/.test(lines[index])) {
        quoteLines.push(lines[index].replace(/^\s*>\s+/, ""));
        index += 1;
      }
      htmlParts.push(
        `<blockquote>${quoteLines
          .map((quoteLine) => applyInlineMarkdown(quoteLine))
          .join("<br/>")}</blockquote>`
      );
      continue;
    }

    if (/^\s*[-*]\s+/.test(line)) {
      const items = [];
      while (index < lines.length && /^\s*[-*]\s+/.test(lines[index])) {
        items.push(lines[index].replace(/^\s*[-*]\s+/, ""));
        index += 1;
      }
      htmlParts.push(
        `<ul>${items
          .map((item) => `<li>${applyInlineMarkdown(item)}</li>`)
          .join("")}</ul>`
      );
      continue;
    }

    if (/^\s*\d+\.\s+/.test(line)) {
      const items = [];
      while (index < lines.length && /^\s*\d+\.\s+/.test(lines[index])) {
        items.push(lines[index].replace(/^\s*\d+\.\s+/, ""));
        index += 1;
      }
      htmlParts.push(
        `<ol>${items
          .map((item) => `<li>${applyInlineMarkdown(item)}</li>`)
          .join("")}</ol>`
      );
      continue;
    }

    const paragraphLines = [];
    while (
      index < lines.length &&
      lines[index].trim() &&
      !lines[index].startsWith("```") &&
      !/^(#{1,6})\s+/.test(lines[index]) &&
      !/^\s*>\s+/.test(lines[index]) &&
      !/^\s*[-*]\s+/.test(lines[index]) &&
      !/^\s*\d+\.\s+/.test(lines[index])
    ) {
      paragraphLines.push(lines[index]);
      index += 1;
    }

    htmlParts.push(
      `<p>${paragraphLines
        .map((paragraphLine) => applyInlineMarkdown(paragraphLine))
        .join("<br/>")}</p>`
    );
  }

  return htmlParts.join("");
}

function StreamingMessageRenderer({
  content,
  className = "bubble-text",
  isStreaming = false,
  enableVisualBuffer = false,
}) {
  const sourceText = content ?? "";
  const [displayText, setDisplayText] = useState(sourceText);
  const frameRef = useRef(null);

  useEffect(() => {
    if (!enableVisualBuffer || !isStreaming) {
      setDisplayText(sourceText);
      return undefined;
    }

    let cancelled = false;

    const animate = () => {
      if (cancelled) return;
      setDisplayText((prev) => {
        if (sourceText.length < prev.length) {
          return sourceText;
        }
        if (prev.length >= sourceText.length) {
          return prev;
        }
        const nextLength = Math.min(prev.length + 32, sourceText.length);
        return sourceText.slice(0, nextLength);
      });
      frameRef.current = window.requestAnimationFrame(animate);
    };

    frameRef.current = window.requestAnimationFrame(animate);

    return () => {
      cancelled = true;
      if (frameRef.current != null) {
        window.cancelAnimationFrame(frameRef.current);
        frameRef.current = null;
      }
    };
  }, [enableVisualBuffer, isStreaming, sourceText]);

  const html = useMemo(() => {
    try {
      return renderMarkdownToHtml(displayText);
    } catch {
      return null;
    }
  }, [displayText]);

  if (html == null) {
    return <p className={className}>{displayText}</p>;
  }

  return (
    <div
      className={`${className} bubble-markdown`}
      dangerouslySetInnerHTML={{ __html: html }}
    />
  );
}

export default memo(StreamingMessageRenderer);
