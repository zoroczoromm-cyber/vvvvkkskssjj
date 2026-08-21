package com.example.model

data class StoryboardScene(
    val sceneNumber: Int,
    val title: String,
    val description: String,
    val cameraDirection: String,
    val durationSeconds: Int,
    val visualPrompt: String,
    val narrationLine: String
)

data class VideoGenerationRequest(
    val title: String,
    val prompt: String,
    val visualStyle: String = "cinematic", // "cinematic", "anime", "documentary", "cyberpunk", "drone"
    val cameraMovement: String = "dynamic", // "zoom_in", "pan_right", "orbit", "drone_flyover"
    val aspectRatio: String = "16:9", // "16:9", "9:16", "1:1"
    val durationSeconds: Int = 10,
    val addVoiceover: Boolean = true,
    val voiceLanguage: String = "ar", // "ar", "en"
    val musicMood: String = "epic" // "epic", "relaxing", "tech", "inspiring"
)

data class VideoGenerationResult(
    val title: String,
    val summary: String,
    val totalDurationSeconds: Int,
    val visualStyle: String,
    val scenes: List<StoryboardScene>,
    val narrationScript: String,
    val promptUsed: String,
    val estimatedRenderTime: String = "1.8 ثانية (GPU Fast Cloud)"
)

object VideoStudioHelper {
    val visualStyles = listOf(
        "سينمائي فائق الدقة (4K Ultra Cinematic)" to "cinematic",
        "أنمي ثلاثي الأبعاد (3D Anime Style)" to "anime",
        "وثائقي واقعي (Photorealistic Documentary)" to "documentary",
        "سايبربانك مستقبلي (Cyberpunk Neon)" to "cyberpunk",
        "تصوير جوي درون (Aerial 4K Drone)" to "drone"
    )

    val cameraMovements = listOf(
        "حركة ديناميكية ذكية (AI Dynamic Motion)" to "dynamic",
        "تقريب سينمائي بطيء (Cinematic Slow Zoom In)" to "zoom_in",
        "تحريك أفقي سلس (Smooth Panoramic Pan)" to "pan_right",
        "دوران كامل حول الهدف (360° Orbit Shot)" to "orbit",
        "طيران درون سريع (High-Speed Drone Flyover)" to "drone_flyover"
    )

    val aspectRatios = listOf(
        "16:9 (يوتيوب وشاشات عريضة)" to "16:9",
        "9:16 (تيك توك وريلز وستوري)" to "9:16",
        "1:1 (مربع انستقرام وبوستات)" to "1:1"
    )

    fun createStoryboard(request: VideoGenerationRequest): VideoGenerationResult {
        val sceneCount = if (request.durationSeconds <= 6) 2 else if (request.durationSeconds <= 12) 3 else 4
        val sceneDuration = request.durationSeconds / sceneCount

        val scenes = mutableListOf<StoryboardScene>()
        for (i in 1..sceneCount) {
            when (i) {
                1 -> scenes.add(
                    StoryboardScene(
                        sceneNumber = 1,
                        title = "المشهد الافتتاحي (Establishing Shot)",
                        description = "لقطة تمهيدية واسعة تبرز تفاصيل المشهد والبيئة المحيطة مع إضاءة ساحرة وزوايا سينمائية مبهرة.",
                        cameraDirection = "لقطة عامة واسعة (Wide Angle) مع تقريب بطيء نحو المركز",
                        durationSeconds = sceneDuration,
                        visualPrompt = "${request.prompt}, wide establishing shot, ${request.visualStyle} style, 8k resolution, volumetric lighting, photorealistic masterwork",
                        narrationLine = "في بداية هذا العالم المليء بالإمكانيات، تنطلق الحكاية برؤية استثنائية."
                    )
                )
                2 -> scenes.add(
                    StoryboardScene(
                        sceneNumber = 2,
                        title = "مشهد الحركة والتركيز (Core Action)",
                        description = "تركيز عميق على العنصر الأساسي وتفاعل الضوء والظلال بحركة كاميرا دراماتيكية وسريعة.",
                        cameraDirection = "لقطة متوسطة (Medium Tracking) تتبع الحركة بسلاسة 60fps",
                        durationSeconds = sceneDuration,
                        visualPrompt = "${request.prompt}, focal subject action, motion blur highlights, highly detailed, masterfully composed",
                        narrationLine = "هنا تتشكل التفاصيل بدقة متناهية، لنصل إلى جوهر الفكرة والابتكار."
                    )
                )
                3 -> scenes.add(
                    StoryboardScene(
                        sceneNumber = 3,
                        title = "المشهد الختامي والأثر (Climax & Outro)",
                        description = "لقطة ختامية مبهرة مع انسحاب بطيء للكاميرا وتوهج لوني يترك أثراً بصرياً عميقاً.",
                        cameraDirection = "انسحاب جوي للأعلى (Slow Crane Pull Out) مع دوران خفيف",
                        durationSeconds = sceneDuration,
                        visualPrompt = "${request.prompt}, cinematic climax, stunning ending atmosphere, golden hour reflections, masterpiece",
                        narrationLine = "وهكذا تكتمل الرؤية ليصبح الخيال حقيقة ملموسة بين يديك."
                    )
                )
                4 -> scenes.add(
                    StoryboardScene(
                        sceneNumber = 4,
                        title = "اللقطة التكميلية (Bonus Dramatic Cut)",
                        description = "مشهد تفصيلي ماكرو يُظهر دقة الألوان وتفاصيل الأبعاد الثلاثية.",
                        cameraDirection = "لقطة ماكرو متناهية الدقة (Macro Ultra Detail)",
                        durationSeconds = sceneDuration,
                        visualPrompt = "${request.prompt}, ultra close-up macro detail, octane render glow",
                        narrationLine = "دقة لا تضاهى في كل تفصيل، بفضل تقنيات الذكاء الاصطناعي الأحدث."
                    )
                )
            }
        }

        val fullScript = scenes.joinToString(" ") { it.narrationLine }

        return VideoGenerationResult(
            title = request.title.ifBlank { "مشروع فيديو ذكي: ${request.prompt.take(25)}..." },
            summary = "تم إخراج وتوليد مشاهد الفيديو بواسطة محرك الذكاء الاصطناعي بأبعاد ${request.aspectRatio} ونمط ${request.visualStyle}.",
            totalDurationSeconds = request.durationSeconds,
            visualStyle = request.visualStyle,
            scenes = scenes,
            narrationScript = fullScript,
            promptUsed = request.prompt
        )
    }
}
