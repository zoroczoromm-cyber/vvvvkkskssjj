package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

data class Persona(
    val id: String,
    val titleArabic: String,
    val subtitleArabic: String,
    val descriptionArabic: String,
    val icon: ImageVector,
    val systemPrompt: String,
    val starterPrompts: List<String>,
    val colorHex: Long,
    val category: String = "عام"
)

object Personas {
    val GENERAL = Persona(
        id = "general",
        titleArabic = "المساعد الشامل الذكي",
        subtitleArabic = "إجابات فورية، حلول شاملة ومحادثة انسيابية",
        descriptionArabic = "مساعدك اليومي المتكامل للإجابة عن التساؤلات العامة، تنظيم الأفكار، وحل المشكلات اليومية بدقة ومرونة.",
        icon = Icons.Default.AutoAwesome,
        systemPrompt = "أنت مساعد ذكاء اصطناعي ودود، فائق الذكاء ومثقف، تجيب باللغة العربية الفصحى الجميلة والواضحة مع تنسيق مرتب.",
        starterPrompts = listOf(
            "ما هي أفضل الاستراتيجيات لتنظيم الوقت وزيادة الإنتاجية اليومية؟",
            "لخص لي أهم فوائد القراءة المستمرة على الدماغ والذاكرة.",
            "كيف أبدأ بتعلم مهارة جديدة من الصفر بفعالية وبأقل وقت؟",
            "اقترح خطة متكاملة لتحقيق التوازن بين العمل والحياة الشخصية."
        ),
        colorHex = 0xFF0D9488,
        category = "عام"
    )

    val CODER = Persona(
        id = "coder",
        titleArabic = "خبير هندسة البرمجيات",
        subtitleArabic = "أكواد نظيفة، معمارية الأنظمة، وتصحيح الأخطاء",
        descriptionArabic = "مهندس ومستشار تقني محترف لكتابة الأكواد، مراجعتها، تحليل الخوارزميات وبناء المعماريات المتقدمة.",
        icon = Icons.Default.Code,
        systemPrompt = "أنت مهندس برمجيات محترف وخبير في هندسة النظم. تشرح المفاهيم البرمجية بوضوح مع أمثلة كود نظيفة وشروحات تفصيلية باللغة العربية مع الحفاظ على مصطلحات الكود بالإنجليزية.",
        starterPrompts = listOf(
            "كيف أصمم بنية تطبيق أندرويد متكاملة بنمط MVVM و Clean Architecture؟",
            "اشرح لي الفرق بين Kotlin Coroutines و Reactive Flow مع أمثلة عملية.",
            "اكتب لي خوارزمية بحث ثنائي في مصفوفة مرتبة مع تحليل التعقيد الزمني.",
            "ما هي أفضل الممارسات لتأمين واجهات برمجة التطبيقات (REST APIs)؟"
        ),
        colorHex = 0xFF3B82F6,
        category = "تقنية وبرمجة"
    )

    val WRITER = Persona(
        id = "writer",
        titleArabic = "الكاتب والمدقق اللغوي",
        subtitleArabic = "صياغة احترافية، تدقيق لغوي وترجمة بلاغية",
        descriptionArabic = "مختص البلاغة وصياغة المقالات، الرسائل الرسمية، التدقيق اللغوي والترجمة الدقيقة.",
        icon = Icons.Default.EditNote,
        systemPrompt = "أنت كاتب محترف ومترجم بارع وخبير في البلاغة واللغة العربية وقواعد الإملاء والأسلوب التعبيري الراقي.",
        starterPrompts = listOf(
            "اكتب لي بريداً إلكترونياً رسمياً لطلب مقابلة عمل لوظيفة مطور برمجيات.",
            "صغ مقالاً جذاباً وموجزاً عن أثر الذكاء الاصطناعي على مستقبل التعليم.",
            "ترجم هذه العبارة إلى لغة عربية أدبية رصينة: 'The journey of a thousand miles begins with a single step'.",
            "ساعدني في تدقيق وتحسين صياغة نص تسويقي لمنتج تقني جديد."
        ),
        colorHex = 0xFF8B5CF6,
        category = "كتابة وترجمة"
    )

    val CREATIVE = Persona(
        id = "creative",
        titleArabic = "مستشار الأعمال والابتكار",
        subtitleArabic = "أفكار مشاريع، دراسات جدوى واستراتيجيات نمو",
        descriptionArabic = "شريكك في التفكير الاستراتيجي وابتكار الحلول التسويقية وخطط النمو للمشاريع الناشئة.",
        icon = Icons.Default.Lightbulb,
        systemPrompt = "أنت مستشار إبداعي واستراتيجي أعمال مبتكر، تطرح أفكاراً خارج الصندوق وتقدم نماذج أعمال وخطط تسويقية عملية ومحفزة.",
        starterPrompts = listOf(
            "اقترح 5 أفكار مشاريع رقمية مربحة وواعدة لعام 2026 مع نموذج العمل.",
            "أريد أسماء إبداعية وحديثة وهوية تسويقية لمنصة بودكاست عربي.",
            "كيف أصمم حملة إطلاق رقمية مؤثرة وناجحة لمنتج تقني جديد؟",
            "ما هي خطوات إعداد خطة تسويق بالمحتوى لزيادة المبيعات بنسبة 50%؟"
        ),
        colorHex = 0xFFF59E0B,
        category = "أعمال وتسويق"
    )

    val LIFE_COACH = Persona(
        id = "coach",
        titleArabic = "رفيق تطوير الذات والإنتاجية",
        subtitleArabic = "عادات إيجابية، إدارة الضغوطات والتركيز",
        descriptionArabic = "مرشد شخصي لبناء العادات القوية، التخلص من التسويف، وتنظيم الأولويات لعيش حياة أكثر إنجازاً وسكينة.",
        icon = Icons.Default.Psychology,
        systemPrompt = "أنت موجه حياة ومرشد تطوير ذات متعاطف وحكيم، تقدم خطوات عملية وإرشادات واضحة للتغلب على التحديات النفسية وبناء العادات.",
        starterPrompts = listOf(
            "كيف أبني عادة الاستيقاظ باكراً بدون الشعور بالإرهاق؟",
            "ما هي أفضل الطرق العملية للتخلص من التسويف والمماطلة؟",
            "خطوات ممارسة اليقظة الذهنية والهدوء أثناء ضغوطات العمل اليومية.",
            "كيف أحافظ على حماسي والتزامي بالمشاريع الطويلة المدى؟"
        ),
        colorHex = 0xFF10B981,
        category = "تطوير الذات"
    )

    val STUDY = Persona(
        id = "study",
        titleArabic = "المعلم والمشرف الأكاديمي",
        subtitleArabic = "تبسيط العلوم والرياضيات والتحضير للاختبارات",
        descriptionArabic = "معلم صبور يشرح المفاهيم المعقدة في الرياضيات، العلوم، الفيزياء والتاريخ بأسلوب شيق ومبسط.",
        icon = Icons.Default.School,
        systemPrompt = "أنت أستاذ ومعلم خبير وصبور، تبسط أصعب المفاهيم العلمية بطريقة شيقة ومفيدة مع أمثلة من واقع الحياة.",
        starterPrompts = listOf(
            "اشرح لي نظرية النسبية لأينشتاين كأن عمري 12 سنة مع أمثلة بصرية.",
            "كيف تعمل الشبكات العصبية الاصطناعية وما هو مبدأ التعلم العميق؟",
            "لخص لي قوانين الحركة لنيوتن وكيف تؤثر في حياتنا اليومية.",
            "طريقة مبسطة لفهم الاحتمالات والتوزيع الطبيعي في علم الإحصاء."
        ),
        colorHex = 0xFFEC4899,
        category = "دراسة وتعليم"
    )

    val LEGAL = Persona(
        id = "legal",
        titleArabic = "المستشار القانوني والإداري",
        subtitleArabic = "صياغة العقود، اللوائح والاستشارات النظامية",
        descriptionArabic = "خبير الصياغة القانونية ومراجعة بنود الاتفاقيات والتوجيه الإداري للشركات والأفراد.",
        icon = Icons.Default.Gavel,
        systemPrompt = "أنت مستشار قانوني وإداري محترف، تشرح المصطلحات النظامية وتصيغ مسودات العقود بدقة ووضوح باللغة العربية مع مراعاة المعايير العامة.",
        starterPrompts = listOf(
            "ما هي البنود الأساسية الواجب توفرها في عقد تقديم خدمات برمجية؟",
            "كيف أصيغ اتفاقية سرية معلومات (NDA) واضحة وشاملة؟",
            "ما هي خطوات توثيق وحماية الملكية الفكرية لتطبيق إلكتروني؟",
            "صغ لي بنداً قانونياً لحل النزاعات والتحكيم في العقود التجارية."
        ),
        colorHex = 0xFF6366F1,
        category = "قانون وإدارة"
    )

    val FITNESS = Persona(
        id = "fitness",
        titleArabic = "مدرب الصحة واللياقة البدنية",
        subtitleArabic = "تغذية صحية، جداول تمارين ونمط حياة حيوي",
        descriptionArabic = "مرشدك الرياضي والصحي لتصميم برامج التمارين المنزلية والنادي، وحساب السعرات الغذائية.",
        icon = Icons.Default.FitnessCenter,
        systemPrompt = "أنت مدرب لياقة بدنية وخبير تغذية رياضية محترف، تقدم نصائح صحية وجداول تدريبية متوازنة تناسب جميع المستويات.",
        starterPrompts = listOf(
            "صمم لي جدول تمارين منزلية لمدة 4 أيام في الأسبوع بدون أوزان.",
            "كيف أحسب احتياجي اليومي من السعرات والبروتين لبناء العضلات؟",
            "ما هي أفضل وجبات ما قبل وبعد التمرين لزيادة الطاقة والتعافي؟",
            "نصائح لتحسين جودة النوم وزيادة معدل حرق الدهون الطبيعي."
        ),
        colorHex = 0xFF14B8A6,
        category = "صحة ولياقة"
    )

    val all = listOf(GENERAL, CODER, WRITER, CREATIVE, LIFE_COACH, STUDY, LEGAL, FITNESS)

    fun getById(id: String): Persona {
        return all.find { it.id == id } ?: GENERAL
    }
}

