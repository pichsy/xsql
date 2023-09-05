package com.pichs.xsql.demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jeremyliao.liveeventbus.LiveEventBus
import com.pichs.xsql.XSql
import com.pichs.xsql.demo.databinding.ActivityMainBinding
import com.pichs.xsql.property.XSqlProperties
import com.pichs.xsql.where.Where
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = XSql.getDBManager(this).getBaseDao(UserInfo::class.java)
        Log.d("XSql", "AbstractDao onCreate: dao： ${dao}")

        binding.insertData.setOnClickListener {
            val name = binding.etData.text.toString()
            dao.insert(UserInfo(name, Random.nextInt(100), "tag"))
        }

        binding.deleteData.setOnClickListener {
            val name = binding.etData.text.toString()

            dao.delete(UserInfo(name))
        }

        binding.updateData.setOnClickListener {
            val name = binding.etData.text.toString()
            dao.update(UserInfo(name, 99))
        }

        binding.queryData.setOnClickListener {
//            val list = dao.queryLastOne()
            val name = binding.etData.text.toString()
            if (name.isEmpty()) {
//                val list = dao.query(Where.Builder().orderByAsc(XSqlProperties.UserInfoData.age).build())
                val list = dao.queryAll()
                binding.tvResult.text = list.joinToString("\n")
            } else {
                val list =
                    dao.query(Where.Builder().notLike(XSqlProperties.UserInfo.name, name).page(5, 3).orderByAsc(XSqlProperties.UserInfo.age).build())
                binding.tvResult.text = list.joinToString("\n")
            }
        }


        LiveEventBus.get("dpc_download_app_progress",Float::class.java).observe(this) {
            Toast.makeText(applicationContext, "收到了：${it}", Toast.LENGTH_SHORT).show()
            binding.tvResult.text = "收到了：${it}"
        }

    }

}