
CREATE TABLE `post` (
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `title` text NOT NULL,
  `content` text NOT NULL,
  `renderedContent` text NOT NULL,
  `announcement` text NOT NULL,
  `postStatus` text NOT NULL,
  `postFormat` text NOT NULL,
  `postType` text NOT NULL,
  `posts_tags_id` text NOT NULL,
  `seoKeywords` text NOT NULL,
  `seoDescription` text NOT NULL,
  `SeoPostData_id` int(11) NOT NULL,
  `permalink` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `postes_likes`
--

CREATE TABLE `postes_likes` (
  `user_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `sympathy` text NOT NULL,
  `client_Ip` text NOT NULL,
  `isAdmin` tinyint(1) NOT NULL,
  `postes_likes_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `posts_tags`
--

CREATE TABLE `posts_tags` (
  `posts_tags_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `seo_post_data`
--

CREATE TABLE `seo_post_data` (
  `seo_post_data_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `ogTitle` text NOT NULL,
  `ogType` text NOT NULL,
  `ogImage` text NOT NULL,
  `ogVideo` text NOT NULL,
  `ogLocale` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `seo_robots_agents`
--

CREATE TABLE `seo_robots_agents` (
  `seo_robots_agents_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `ogTitle` text NOT NULL,
  `ogType` text NOT NULL,
  `ogImage` text NOT NULL,
  `ogVideo` text NOT NULL,
  `ogLocale` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `settings`
--

CREATE TABLE `settings` (
  `settings_id` int(11) NOT NULL,
  `_key` text NOT NULL,
  `_value` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `stored_files`
--

CREATE TABLE `stored_files` (
  `stored_files_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` text NOT NULL,
  `name` text NOT NULL,
  `path` text NOT NULL,
  `size` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `tags`
--

CREATE TABLE `tags` (
  `tag_id` int(11) NOT NULL,
  `name` text NOT NULL,
  `posts_tags_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `email` text NOT NULL,
  `password` text NOT NULL,
  `role` text NOT NULL,
  `posts` text NOT NULL,
  `storedFiles` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `visits`
--

CREATE TABLE `visits` (
  `visit_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `client_Ip` text NOT NULL,
  `isAdmin` tinyint(1) NOT NULL,
  `userAgent` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`post_id`);

--
-- Index pour la table `postes_likes`
--
ALTER TABLE `postes_likes`
  ADD PRIMARY KEY (`postes_likes_id`);

--
-- Index pour la table `posts_tags`
--
ALTER TABLE `posts_tags`
  ADD PRIMARY KEY (`posts_tags_id`);

--
-- Index pour la table `seo_post_data`
--
ALTER TABLE `seo_post_data`
  ADD PRIMARY KEY (`seo_post_data_id`);

--
-- Index pour la table `stored_files`
--
ALTER TABLE `stored_files`
  ADD PRIMARY KEY (`stored_files_id`);

--
-- Index pour la table `tags`
--
ALTER TABLE `tags`
  ADD PRIMARY KEY (`tag_id`);

--
-- Index pour la table `visits`
--
ALTER TABLE `visits`
  ADD PRIMARY KEY (`visit_id`);
COMMIT;
